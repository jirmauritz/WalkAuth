package cz.muni.fi.walkauth;

/**
 * Math utilities.
 */
public final class MathUtils {

	private MathUtils() {
		throw new AssertionError("This is a noninstantiable utility class.");	
	}
	
	/**
	 * Calculates square error for lists of predicted and actual values.
	 * 
	 * The formula is 0.5 * sum_i((predicted[i] - actual[i])^2).
	 * 
	 * @param predictedValues list of predicted values
	 * @param actualValues list of actual values
	 * @return error according to the formula above
	 */
	public static double squareError(double[] predictedValues, double[] actualValues) {
		assert predictedValues.length == actualValues.length;
		
		double error = 0;
		double diff;
		for (int i = 0; i < predictedValues.length; i++) {
			diff =  predictedValues[i] - actualValues[i];
			error += diff * diff;
		}
		error *= 0.5;
		return error;
	}
	
}
