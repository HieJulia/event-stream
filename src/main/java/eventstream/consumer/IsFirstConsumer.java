package eventstream.consumer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eventstream.dao.StreamDao;
import eventstream.domain.LogEvent;
import eventstream.domain.Stream;
import eventstream.util.ApplicationContextProvider;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
/**
 * 
 *
 * @author Nilesh Bhosale
 */
@Component
public class IsFirstConsumer extends DefaultConsumer{
	private StreamDao streamDao;

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public IsFirstConsumer(Channel channel) {
		super(channel);
		
		ApplicationContextProvider appContext = new ApplicationContextProvider();
		streamDao = appContext.getApplicationContext().getBean("streamDao", StreamDao.class);
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
			byte[] body) throws IOException {
		String message = new String(body, "UTF-8");
		System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
		
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println( message);
		LogEvent logEvent = objectMapper.readValue(message, LogEvent.class);
		System.out.println( logEvent.getNoun()+" "+logEvent.getVerb()+" "+logEvent.getUserid());
		if(streamDao.findByNounAndVerbAndUserid(logEvent.getNoun(),logEvent.getVerb(),logEvent.getUserid()).size()==1){
			try {
				okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, message);
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
			
		};

	}
}
