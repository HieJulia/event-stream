package eventstream.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eventstream.consumer.IsExceedingLimit;
import eventstream.consumer.IsFirstConsumer;
import eventstream.consumer.ThisButNotThis;
import eventstream.dao.StreamDao;
import eventstream.domain.Rule;
import eventstream.domain.Stream;
import eventstream.exception.InvalidRequestException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Created by Nilesh Bhosale
 */
@RestController()
@RequestMapping("/apis/v1/")
public class DummyController {

	@Autowired
	private StreamDao streamDao;

	private static final String EXCHANGE_NAME = "topic_logs";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	Connection connection = null;
	Channel channel = null;

	public DummyController() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * Add an event to stream
	 */
	@RequestMapping(value = "/stream", method = RequestMethod.POST)
	@ResponseBody
	public Stream addEvent(@RequestBody @Valid Stream stream, BindingResult bindingResult) throws IOException {

		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException("Invalid stream", bindingResult);
		}

		stream = streamDao.save(stream);

		OkHttpClient client = new OkHttpClient();
		System.out.println("Making http call now");

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, objectMapper.writeValueAsString(stream));
			okhttp3.Request request = new okhttp3.Request.Builder().url("http://localhost:8080/apis/v1/logEvent")
					.post(body).build();
			okhttp3.Response response;

			response = client.newCall(request).execute();

			// test
			System.out.println(response.body().string());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stream;

	}

	@RequestMapping(value = "/logEvent", method = RequestMethod.POST)
	@ResponseBody
	public String logEvent(@RequestBody @Valid Stream streamEvent, BindingResult bindingResult) {

		String response = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String routingKey = streamEvent.getNoun() + "." + streamEvent.getVerb();
			String message = objectMapper.writeValueAsString(streamEvent);

			channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
			response = " [x] Sent '" + routingKey + "':'" + message + "'";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	@RequestMapping(value = "/createRule", method = RequestMethod.POST)
	@ResponseBody
	public String createRule(@RequestBody @Valid Rule rule, BindingResult bindingResult) throws Exception {
		String response = "listening";

		String queueName = channel.queueDeclare().getQueue();

		channel.queueBind(queueName, EXCHANGE_NAME, rule.getNoun() + "." + rule.getVerb());

		Consumer consumer = new ThisButNotThis(channel);

		channel.basicConsume(queueName, true, consumer);

		return response;
	}

	@RequestMapping(value = "/dummy", method = RequestMethod.POST)
	@ResponseBody
	public String receiveMessage(@RequestBody @Valid Stream streamEvent, BindingResult bindingResult) throws Exception {
		System.out.println("###############################");
		System.out.println("Notification/Alert");
		System.out.println(streamEvent);
		System.out.println("###############################");
		return "Success";
	}

}
