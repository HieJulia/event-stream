package eventstream.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import eventstream.domain.Response;
import eventstream.domain.Stream;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@RestController()
@RequestMapping("/apis/v1/")
public class LogEventController {

	private static final String EXCHANGE_NAME = "topic_logs";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
			logger.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/log-event", method = RequestMethod.POST)
	@ResponseBody
	public Response logEvent(@RequestBody @Valid Stream streamEvent, BindingResult bindingResult) {

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String routingKey = streamEvent.getNoun() + "." + streamEvent.getVerb();
			String message = objectMapper.writeValueAsString(streamEvent);

			channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		Response response = new Response(); 
		response.setStatus("success");
		return response;
	}

}
