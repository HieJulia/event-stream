package eventstream.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class Event {

	private long id;
	private String noun;
	private String verb;
	private Integer userid;
	private String ts;
	private String latlong;

	private int timespent;
	@JsonIgnore
	private Object properties;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getLatlong() {
		return latlong;
	}

	public void setLatlong(String latlong) {
		this.latlong = latlong;
	}

	public int getTimespent() {
		return timespent;
	}

	public void setTimespent(int timespent) {
		this.timespent = timespent;
	}

	@JsonIgnore
	public Object getProperties() {
		return properties;
	}

	public void setProperties(Object properties) {
		this.properties = properties;
	}

}
