package eventstream.consumer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eventstream.domain.DummyLogModel;
import eventstream.domain.Event;
import eventstream.domain.Rule;
import eventstream.domain.TwoEventRule;
import eventstream.util.ApplicationContextProvider;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class ThisButNotThis extends DefaultConsumer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static Timer timer;
	private static Event firstEvent = null;
	private static TwoEventRule rule;
	private static Boolean waitingForSecondEvent = false;

	public ThisButNotThis(Channel channel, TwoEventRule rule) {
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
			//Check if event has occurred 
			if (waitingForSecondEvent) {
				//Forward alert 
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					DummyLogModel dummyLogModel = new DummyLogModel();
					dummyLogModel.setAlertType(rule.getAlertType());
					dummyLogModel.setAlertuser(rule.getAlertUser());
					dummyLogModel.setData(objectMapper.writeValueAsString(firstEvent));

					okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON,
							objectMapper.writeValueAsString(dummyLogModel));
					okhttp3.Request request = new okhttp3.Request.Builder()
							.url(ApplicationContextProvider.bundle.getString("api.dummy.logger")).post(requestBody).build();
					okhttp3.Response response;

					OkHttpClient client = new OkHttpClient();
					response = client.newCall(request).execute();
				} catch (IOException e) {
					logger.error(e.getMessage());
				} finally {
					//Clear all values
					waitingForSecondEvent = false;
					firstEvent = null;
					if (timer != null)
						timer.cancel();
				}

			}

		}
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		String message = new String(body, "UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		Event logEvent = objectMapper.readValue(message, Event.class);

		//Check if its the first event then start timer
		if (envelope.getRoutingKey().equals(rule.getNoun() + "." + rule.getVerb()) && firstEvent == null) {
			firstEvent = logEvent;
			timer = new Timer();
			timer.schedule(new CheckLimit(), rule.getTimeLimit() * 1000); 
			// Seconds to milliseconds
			waitingForSecondEvent = true;
		} else if (envelope.getRoutingKey().equals(rule.getNounNotThis() + "." + rule.getVerbNotThis()) && waitingForSecondEvent) {
			//IF its the second element and first element is waiting. Clear the values
			waitingForSecondEvent = false;
			firstEvent = null;
			if (timer != null)
				timer.cancel();
		} 

	}
}
