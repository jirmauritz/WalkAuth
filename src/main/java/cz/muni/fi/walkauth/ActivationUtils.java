package cz.muni.fi.walkauth;

import static java.lang.Math.tanh;

/**
 * Functions for computing activity function and its derivatives.
 */
public class ActivationUtils {

	private final double amplitude = 1.7159;
	private final double frequency = 0.6666667;

	private ActivationUtils() {
		throw new AssertionError("This is a noninstantiable utility class.");
	}
	
	public double activationFunction(double x) {
		return amplitude * tanh(frequency * x);
	}
	
	public double activationFunctionFirstDerivative(double x) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public double activationFunctionSecondDerivative(double x) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

}
