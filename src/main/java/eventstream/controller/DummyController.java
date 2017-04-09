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
public class DummyController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/dummy-logger", method = RequestMethod.POST)
	@ResponseBody
	public String receiveMessage(@RequestBody @Valid Stream streamEvent, BindingResult bindingResult) throws Exception {
		System.out.println("###############################");
		System.out.println("Notification/Alert");
		System.out.println(streamEvent);
		System.out.println("###############################");
		return "Success";
	}

}
