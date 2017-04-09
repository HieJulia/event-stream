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
public class StreamController {

	@Autowired
	private StreamDao streamDao;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/*
	 * Add an event to stream
	 */
	@RequestMapping(value = "/stream", method = RequestMethod.POST)
	@ResponseBody
	public Stream addEvent(@RequestBody @Valid Stream stream, BindingResult bindingResult) {

		// Check for validation error in request
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException("Invalid stream", bindingResult);
		}

		// Save the stream
		stream = streamDao.save(stream);

		// Trigger a log event call to send event to listening queues

		OkHttpClient client = new OkHttpClient();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, objectMapper.writeValueAsString(stream));
			okhttp3.Request request = new okhttp3.Request.Builder().url("http://localhost:8080/apis/v1/log-event")
					.post(body).build();
			okhttp3.Response response;
			response = client.newCall(request).execute();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return stream;

	}

}
