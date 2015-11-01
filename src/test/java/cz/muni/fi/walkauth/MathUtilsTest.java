package cz.muni.fi.walkauth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 * Unit test for MathUtils.
 */
public class MathUtilsTest {
	
	public MathUtilsTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of squareError method.
	 */
	@org.junit.Test
	public void testSquareError() {
		System.out.println("hyperbolicTangens");
		double[] predictedValues = {1.1, 1.2, -0.8};
		double[] actualValues = {1.3, 1.2, -0.85};
		double expError = 0.02125;
		double error = MathUtils.squareError(predictedValues, actualValues);
		assertEquals(expError, error, 0.00000001);
	}
	
}
