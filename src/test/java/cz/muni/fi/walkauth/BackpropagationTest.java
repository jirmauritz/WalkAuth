package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for backpropagation algorithm.
 */
public class BackpropagationTest {
	
	
	private static final double EPSILON = 0.001;
	
	private static void assertMatriciesAlmostEqual(Matrix m1, Matrix m2) {
		assertEquals(m1.getRowCount(), m2.getRowCount());
		assertEquals(m1.getColCount(), m2.getColCount());
		
		for (int i = 0; i < m1.getRowCount(); i++) {
            for (int j = 0; j < m1.getColCount(); j++) {
                assertEquals(m1.get(i, j), m2.get(i, j), EPSILON);
            }
        }
    }
	
	private static void assertMatrixListsAlmostEqual(Matrix[] ml1, Matrix[] ml2) {
		assertEquals(ml1.length, ml2.length);
		for (int i = 0; i < ml1.length; i++) {
			assertMatriciesAlmostEqual(ml1[i], ml2[i]);
		}
	}


	@BeforeMethod
	public void setUp() {
	}
	
	/**
	 * Test of backpropagation for a no sample.
	 */
	@Test
	public void test_no_sample() {
		// given
		NeuralNetwork ANY_NETWORK = new NeuralNetwork(1, 1);
		Sample[] data = {};
		
		// action
		System.out.println("test no sample");
		Matrix[] gradient = NeuralNetworkLearning.backpropagation(ANY_NETWORK, data);
		System.out.println("***");
		
		// assert
		Matrix[] expectedGradient = {Matrix.zeros(1, 2)};
		assertMatrixListsAlmostEqual(gradient, expectedGradient);
	}
	
	/**
	 * Test of backpropagation for a simple situation with zero gradient.
	 */
	@Test
	public void test_zero_gradient_for_positive_positive() {
		// given
		NeuralNetwork neuralNetwork = new NeuralNetwork(new Matrix[] {
			new Matrix(new double[][] {{1.0, 0.0}})
		});
		double[] ANY_INPUT = new double[]{0};
		Sample[] data = {new Sample(true, ANY_INPUT)};
		
		// action
		Matrix[] gradient = NeuralNetworkLearning.backpropagation(neuralNetwork, data);
		
		// assert
		Matrix[] expectedGradient = {Matrix.zeros(1, 2)};
		assertMatrixListsAlmostEqual(gradient, expectedGradient);
	}
	
	
}

