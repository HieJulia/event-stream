package eventstream.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

@Component
public class IsExceedingLimit extends DefaultConsumer{
	private StreamDao streamDao;

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private static ArrayList<LogEvent> logEvents = new ArrayList();
	
	private static Timer timer;
	
	class CheckLimit extends TimerTask
	{
		@Override
		public void run() {
			System.out.println("Run Me ~");
			if(logEvents.size()>=5){
				int sum=0;
				for(int i=0;i<logEvents.size();i++){
					//sum+=logEvents.get(i).getProperties().getValue();
					sum+=5000;
				}
				if(sum>=20000){
					try {
						ObjectMapper objectMapper= new ObjectMapper();
						okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, objectMapper.writeValueAsBytes(logEvents));
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
				}else{
					System.out.println("============Well within limit===============");
				}
				
			}
			System.out.println("============Completed===============");
			logEvents.clear();
					
		}
	}
	
	public IsExceedingLimit(Channel channel) {
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
		
		if(logEvents.size()==0){
			logEvents.add(logEvent);
			timer = new Timer();
			timer.schedule(new CheckLimit(), 20000);
		}else{
			logEvents.add(logEvent);
		}
	}
}
