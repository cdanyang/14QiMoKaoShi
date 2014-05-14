import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to be sent over socket for computing
 */
public class ComputeUnit implements Serializable {

	/**
	 * numbers to compute
	 */
	ArrayList<Double> numbersToCompute = new ArrayList<Double>();
	/**
	 * The compute result
	 */
	double sum;

	public void addNumbersToCompute(List<Double> numbers) {
		numbersToCompute.addAll(numbers);
	}

	public void setSum(double sum) {
		this.sum = sum;
	}
}
