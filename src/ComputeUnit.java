import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ComputeUnit implements Serializable{

	ArrayList<Double> numbersToCompute = new ArrayList<Double>();
	double sum;
	
	public void addNumbersToCompute(List<Double> numbers) {
		numbersToCompute.addAll(numbers);
	}
	
	public void setSum(double sum) {
		this.sum = sum;
	}
}
