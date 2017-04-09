package eventstream.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eventstream.util.JpaConverterJson;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@Entity
@Table(name = "stream")
public class Stream {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	@Size(min = 2, max = 10)
	private String noun;

	@NotNull
	@Size(min = 2, max = 10)
	private String verb;

	@NotNull
	private Integer userid;

	@NotNull
	private String ts;

	@NotNull
	private String latlong;

	@NotNull
	private int timespent;

	@Column(name = "properties")
	@Convert(converter = JpaConverterJson.class)
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

	public Object getProperties() {
		return properties;
	}

	public void setProperties(Object properties) {
		this.properties = properties;
	}

}
