package eventstream.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class PaymentEvent extends Event {

	private Payment properties;
	
	@JsonProperty("brand")
	public Payment getProperties() {
		return properties;
	}

	public void setProperties(Payment properties) {
		this.properties = properties;
	}

}
