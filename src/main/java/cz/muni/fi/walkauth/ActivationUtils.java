package cz.muni.fi.walkauth;

import static java.lang.Math.tanh;

/**
 * Functions for computing activity function and its derivatives.
 */
public class ActivationUtils {

	private static final double AMPLITUDE = 1.7159;
	private static final double FREQUENCY = 0.6666667;

	private ActivationUtils() {
		throw new AssertionError("This is a noninstantiable utility class.");
	}
	
	public static double activationFunction(double x) {
		return AMPLITUDE * tanh(FREQUENCY * x);
	}
	
	public double activationFunctionFirstDerivative(double x) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

}
