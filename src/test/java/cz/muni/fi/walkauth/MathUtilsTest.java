package cz.muni.fi.walkauth;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Unit test for MathUtils.
 */
public class MathUtilsTest {


	/**
	 * Test of squareError method.
	 */
	@Test
	public void testSquareError() {
		double[] predictedValues = {1.1, 1.2, -0.8};
		double[] actualValues = {1.3, 1.2, -0.85};
		double expError = 0.02125;
		double error = MathUtils.squareError(predictedValues, actualValues);
		assertEquals(expError, error, 0.00000001);
	}
	
}
