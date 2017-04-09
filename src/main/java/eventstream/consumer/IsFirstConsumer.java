package eventstream.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eventstream.dao.StreamDao;
import eventstream.domain.DummyLogModel;
import eventstream.domain.Event;
import eventstream.domain.Rule;
import eventstream.util.ApplicationContextProvider;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class IsFirstConsumer extends DefaultConsumer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private StreamDao streamDao;
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static Rule rule;

	public IsFirstConsumer(Channel channel, Rule rule) {
		super(channel);
		this.rule = rule;

		// Get the database DAO from Spring Application Context
		ApplicationContextProvider appContext = new ApplicationContextProvider();
		streamDao = appContext.getApplicationContext().getBean("streamDao", StreamDao.class);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		String message = new String(body, "UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		Event logEvent = objectMapper.readValue(message, Event.class);

		// Check if this is the first event by noun, verb and userid
		if (streamDao.findByNounAndVerbAndUserid(logEvent.getNoun(), logEvent.getVerb(), logEvent.getUserid())
				.size() == 1) {
			// Forward the Notification
			try {
				DummyLogModel dummyLogModel = new DummyLogModel();
				dummyLogModel.setAlertType(rule.getAlertType());
				dummyLogModel.setAlertuser(rule.getAlertUser());
				dummyLogModel.setData(objectMapper.writeValueAsString(logEvent));

				okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON,
						objectMapper.writeValueAsString(dummyLogModel));
				okhttp3.Request request = new okhttp3.Request.Builder()
						.url("http://localhost:8080/apis/v1/dummy-logger").post(requestBody).build();
				okhttp3.Response response;

				OkHttpClient client = new OkHttpClient();
				response = client.newCall(request).execute();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		;
	}
}
