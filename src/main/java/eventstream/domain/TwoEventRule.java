package eventstream.domain;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class TwoEventRule extends Rule {
	private String nounNotThis;
	private String verbNotThis;
	private int timeLimit;

	public String getNounNotThis() {
		return nounNotThis;
	}

	public void setNounNotThis(String nounNotThis) {
		this.nounNotThis = nounNotThis;
	}

	public String getVerbNotThis() {
		return verbNotThis;
	}

	public void setVerbNotThis(String verbNotThis) {
		this.verbNotThis = verbNotThis;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

}
