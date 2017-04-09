package eventstream.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eventstream.dao.StreamDao;
import eventstream.domain.DummyLogModel;
import eventstream.domain.PaymentEvent;
import eventstream.domain.PaymentRule;
import eventstream.domain.Rule;
import eventstream.util.ApplicationContextProvider;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class IsExceedingLimit extends DefaultConsumer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	// stores the events stack
	private static ArrayList<PaymentEvent> eventList = new ArrayList<PaymentEvent>();
	// maintains the timer while stacking events
	private static Timer timer;
	// contans the rules for operation
	private static PaymentRule rule;

	public IsExceedingLimit(Channel channel, PaymentRule rule) {
		super(channel);
		this.rule = rule;
	}

	/**
	 * 
	 *
	 * @author Nilesh Bhosale
	 */
	class CheckLimit extends TimerTask {
		@Override
		public void run() {

			if (eventList.size() >= rule.getMin()) {

				int sum = 0;
				for (PaymentEvent event : eventList) {
					sum += event.getProperties().getValue();
				}

				if (sum >= rule.getMinTotal()) {
					// Value is overlimit forward to alerts
					try {
						ObjectMapper objectMapper = new ObjectMapper();

						DummyLogModel dummyLogModel = new DummyLogModel();
						dummyLogModel.setAlertType(rule.getAlertType());
						dummyLogModel.setAlertuser(rule.getAlertUser());
						dummyLogModel.setData(objectMapper.writeValueAsString(eventList));

						okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON,
								objectMapper.writeValueAsString(dummyLogModel));
						okhttp3.Request request = new okhttp3.Request.Builder()
								.url(ApplicationContextProvider.bundle.getString("api.dummy.logger")).post(requestBody)
								.build();
						okhttp3.Response response;

						OkHttpClient client = new OkHttpClient();
						response = client.newCall(request).execute();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}

			}
			// Completed checking clear old values
			eventList.clear();

		}
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {

		String message = new String(body, "UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		PaymentEvent logEvent = objectMapper.readValue(message, PaymentEvent.class);

		if (eventList.size() == 0) {
			// Check if new event is found for first time.

			// Maintain all the events till the timer goes off.
			eventList.add(logEvent);
			timer = new Timer();
			timer.schedule(new CheckLimit(), rule.getTimeLimit() * 1000);
			// Seconds to milliseconds

		} else {
			// Another event under timelimit. Stack it to the list.
			eventList.add(logEvent);
		}
	}
}
