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
 * 
 *
 * @author Nilesh Bhosale
 */
@RestController()
@RequestMapping("/apis/v1/")
public class LogEventController {

	@Autowired
	private StreamDao streamDao;

	private static final String EXCHANGE_NAME = "topic_logs";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	Connection connection = null;
	Channel channel = null;

	public LogEventController() {
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

	@RequestMapping(value = "/log-event", method = RequestMethod.POST)
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


}
