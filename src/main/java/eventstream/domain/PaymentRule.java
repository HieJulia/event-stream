package eventstream.domain;
/**
 * 
 *
 * @author Nilesh Bhosale
 */
public class PaymentRule extends Rule {
	private int min;
	private int timeLimit;
	private int minTotal;

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getMinTotal() {
		return minTotal;
	}

	public void setMinTotal(int minTotal) {
		this.minTotal = minTotal;
	}
}
