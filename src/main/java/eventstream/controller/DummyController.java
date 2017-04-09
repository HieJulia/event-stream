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

import eventstream.domain.DummyLogModel;
import eventstream.domain.Response;

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
	public Response receiveMessage(@RequestBody @Valid DummyLogModel dumymLogModel, BindingResult bindingResult)
			throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		logger.info("###############################");
		if (dumymLogModel.getAlertType().equals("Notification"))
			logger.info("Notification");
		if (dumymLogModel.getAlertType().equals("Alert"))
			logger.info("Alert");
		logger.info(" for " + dumymLogModel.getAlertuser());
		logger.info(" with data ");
		logger.info(objectMapper.writeValueAsString(dumymLogModel));
		logger.info("###############################");

		Response response = new Response();
		response.setStatus("success");
		return response;
	}

}
