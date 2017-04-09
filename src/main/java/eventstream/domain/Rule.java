package eventstream.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
	private String ruleType;

	private String noun;

	private String verb;

	private String alertUser;

	private String alertType;
	
	public Rule() {
	}

	public String getNoun() {
		return noun;
	}

	public void setNoun(String noun) {
		this.noun = noun;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getAlertUser() {
		return alertUser;
	}

	public void setAlertUser(String alertUser) {
		this.alertUser = alertUser;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

}
