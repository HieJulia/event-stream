package eventstream.consumer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eventstream.consumer.IsExceedingLimit.CheckLimit;
import eventstream.dao.StreamDao;
import eventstream.domain.LogEvent;
import eventstream.domain.Stream;
import eventstream.util.ApplicationContextProvider;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

@Component
public class ThisButNotThis extends DefaultConsumer {


	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static Timer timer;

	private static LogEvent firstEvent = null;

	private static Boolean waitingForSecondEvent = false;

	public ThisButNotThis(Channel channel) {
		super(channel);

	}

	class CheckLimit extends TimerTask {
		@Override
		public void run() {
			System.out.println("Run Me ~");
			if (waitingForSecondEvent) {
				waitingForSecondEvent = false;
				firstEvent = null;
				if (timer != null)
					timer.cancel();
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON,
							objectMapper.writeValueAsBytes(firstEvent));
					okhttp3.Request request = new okhttp3.Request.Builder().url("http://localhost:8080/apis/v1/dummy")
							.post(requestBody).build();
					okhttp3.Response response;

					OkHttpClient client = new OkHttpClient();
					response = client.newCall(request).execute();
					
					// test
					System.out.println(response.body().string());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("============Completed===============");

		}
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		String message = new String(body, "UTF-8");
		System.out.println(" [x] Received This but not this '" + envelope.getRoutingKey() + "':'" + message + "'");

		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(message);
		LogEvent logEvent = objectMapper.readValue(message, LogEvent.class);
		System.out.println(
				logEvent.getNoun() + " " + logEvent.getVerb() + " " + logEvent.getUserid() + " " + firstEvent);
		System.out.println(envelope.getRoutingKey().equals("bill.pay") && firstEvent == null);
		System.out.println(envelope.getRoutingKey().equals("bill1.pay") );
		
		if (envelope.getRoutingKey().equals("bill.pay") && firstEvent == null) {
			firstEvent = logEvent;
			timer = new Timer();
			timer.schedule(new CheckLimit(), 20000);
			waitingForSecondEvent = true;
		} else if (envelope.getRoutingKey().equals("bill1.pay") ) {
			waitingForSecondEvent = false;
			firstEvent = null;
			if (timer != null)
				timer.cancel();
		}else{
			System.out.println("*********************************************");
		}

	}
}
