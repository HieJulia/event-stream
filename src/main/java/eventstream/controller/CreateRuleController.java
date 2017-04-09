package eventstream.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import eventstream.consumer.IsExceedingLimit;
import eventstream.consumer.IsFirstConsumer;
import eventstream.consumer.ThisButNotThis;
import eventstream.dao.StreamDao;
import eventstream.domain.PaymentRule;
import eventstream.domain.Response;
import eventstream.domain.Rule;
import eventstream.domain.TwoEventRule;
import okhttp3.MediaType;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@RestController()
@RequestMapping("/apis/v1/")
public class CreateRuleController {

	@Autowired
	private StreamDao streamDao;

	private static final String EXCHANGE_NAME = "topic_logs";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	Connection connection = null;
	Channel channel = null;

	public CreateRuleController() {
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

	@RequestMapping(value = "/create-rule", method = RequestMethod.POST)
	@ResponseBody
	public Response createRule(@RequestBody String ruleString) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		Rule rule = objectMapper.readValue(ruleString, Rule.class);

		String queueName = channel.queueDeclare().getQueue();

		channel.queueBind(queueName, EXCHANGE_NAME, rule.getNoun() + "." + rule.getVerb());
		Consumer consumer = null;

		switch (rule.getRuleType()) {
		case "IsFirstConsumer":
			consumer = new IsFirstConsumer(channel, rule);
			break;
		case "IsExceedingLimit":
			consumer = new IsExceedingLimit(channel, objectMapper.readValue(ruleString, PaymentRule.class));
			break;
		case "ThisButNotThis":
			consumer = new ThisButNotThis(channel, objectMapper.readValue(ruleString, TwoEventRule.class));
			break;

		}

		channel.basicConsume(queueName, true, consumer);

		Response response = new Response();
		response.setStatus("success");
		return response;
	}

}
