package util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A list of double values implemented along with basic 
 * statistical operations.
 * 
 * @author Pablo A. Sampaio
 */
public class DoubleList {
	private List<Double> numbers;
	
	public DoubleList() {
		numbers = new LinkedList<Double>();
	}
	
	public DoubleList(int size) {
		numbers = new ArrayList<Double>(size);
	}
	
	public void add(double n) {
		numbers.add(n);
	}

	public void addAll(DoubleList list) {
		numbers.addAll(list.numbers);
	}

	public int size() {
		return numbers.size();
	}
	
	public double get(int index) {
		return numbers.get(index);
	}
	
	public double max() {
		double max = Double.NEGATIVE_INFINITY;
		
		for (double x : numbers) {
			if (x > max) {
				max = x;
			}
		}
		
		return max;
	}
	
	public double max(DoubleList weights) {
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0; i < this.size(); i++) {
			if ( numbers.get(i) * weights.get(i) > max) {
				max = numbers.get(i) * weights.get(i);
			}
		}
		
		return max;
	}
	
	public double min() {
		double min = Double.POSITIVE_INFINITY;
		
		for (double x : numbers) {
			if (x < min) {
				min = x;
			}
		}
		
		return min;
	}
	
	public double sum() {
		double sum = 0.0d;
		
		for (double x : numbers) {
			sum += x;
		}
		
		return sum;		
	}
	
	// average
	public double mean() {
		return sum() / size();		
	}
	
	public double generalizedMean(double p, DoubleList weights) {
		double sum = 0.0d;
		
		for (int i = 0; i < numbers.size(); i ++) {
			sum += Math.pow(numbers.get(i),p) * weights.get(i);
		}
		
		return Math.pow(sum/weights.sum() , 1.0d / p);
	}
	
	public double specialMean() {
		double squaredSum = 0.0d;
		
		for (int i = 0; i < numbers.size(); i ++) {
			squaredSum += Math.pow(numbers.get(i),2);
		}
		
		return (squaredSum/numbers.size()) / mean();
	}
	
	//average divs without weights
//	public double generalizedMean2(double p, DoubleList weights) {
//		double sum = 0.0d;
//		
//		for (int i = 0; i < numbers.size(); i ++) {
//			sum += Math.pow(numbers.get(i),p) * weights.get(i);
//		}
//		
//		return Math.pow(weights.size() , 1.0d / p);
//	}

	public double variance() {
		double sumSquareDifferences = 0.0d;
		
		double average = mean();
		double difference;
		
		for (double n : numbers) {
			difference = (n - average);
			sumSquareDifferences += (difference * difference);
		}
		
		return sumSquareDifferences / (size() - 1);		
	}

	public double standardDeviation() {
		return Math.sqrt(variance());		
	}

	public String toString() {
		return numbers.toString();
	}
	
}
