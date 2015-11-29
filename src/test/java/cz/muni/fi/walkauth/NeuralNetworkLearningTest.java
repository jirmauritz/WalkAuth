package cz.muni.fi.walkauth;

import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test is to verify that the methods of static class NeuralNetworkLearning
 * behaves correctly.
 *
 * @author Jiri Mauritz
 */
public class NeuralNetworkLearningTest {
	
	private final double DOUBLE_EPS = 0.0000001;

	@BeforeMethod
	public void beforeMethod() {
	}

	@Test
	public void testInitWeights() {
		NeuralNetwork network = new NeuralNetwork(100, 50, 30, 1);
		network = NeuralNetworkLearning.initializeWeights(network);
		Matrix[] weights = network.getWeights();
		// check first layer
		double value = Math.sqrt(3.0 / 100.0);
		for (int row = 0; row < weights[0].getRowCount(); row++) {
			for (int col = 0; col < weights[0].getColCount() - 1; col++) {
				double actual = weights[0].getValues()[row][col];
				assertAlmostEqual(value, actual, "failed for index [" + row + "][" + col + "]");
				
			}
			// bias
			assertAlmostEqual(1.0, weights[0].getValues()[row][weights[0].getColCount() - 1], "failed for bias");
		}
	}
	
	private void assertAlmostEqual(double exp, double act, String msg) {
		Assert.assertTrue(Math.abs(act - exp) < DOUBLE_EPS, msg);
	}
}
