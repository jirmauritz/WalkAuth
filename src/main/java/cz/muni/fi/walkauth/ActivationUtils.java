package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import static java.lang.Math.tanh;

/**
 * Functions for computing activity function and its derivatives.
 */
public class ActivationUtils {

	static final double AMPLITUDE = 1.7159;
	static final double STEEPNESS = 0.6666667;

	private ActivationUtils() {
		throw new AssertionError("This is a noninstantiable utility class.");
	}
	
	public static double activationFunction(double x) {
		return AMPLITUDE * tanh(STEEPNESS * x);
	}

	/**
	 * Compute derivative of activation function.
	 * 
	 * @param y function value at the point where we want to get the derivative
	 * @return value of first derivative
	 */
	public static double activationFunctionDerivative(double y) {
		return (AMPLITUDE - y) * (AMPLITUDE + y) * STEEPNESS / AMPLITUDE;
	}
	
	/**
	 * Return value of label of given sample.
	 * 
	 * @param sample sample for which to compute the label value
	 * @return value of positive/negative label
	 */
	public static double labelValue(Sample sample) {
		return sample.isPositiveUserData() ? 1.0 : -1.0;
			//ActivationUtils.AMPLITUDE : -ActivationUtils.AMPLITUDE;	
	}

}
