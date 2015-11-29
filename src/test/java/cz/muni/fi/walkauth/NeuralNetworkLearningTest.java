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

	@BeforeMethod
	public void beforeMethod() {
	}

	@Test
	public void testInitWeights() {
		NeuralNetwork network = new NeuralNetwork(100, 50, 30, 1);
		network = NeuralNetworkLearning.initializeWeights(network);
		Matrix[] weights = network.getWeights();
		System.out.println(Arrays.toString(weights));
		// check first layer
		double range = Math.sqrt(3.0 / 100.0);
		for (int row = 0; row < weights[0].getRowCount(); row++) {
			// values
			for (int col = 0; col < weights[0].getColCount(); col++) {
				double value = weights[0].getValues()[row][col];
				assertInInterval(range, value, "failed for index [" + row + "][" + col + "]");
				
			}
		}
	}
	
	private void assertInInterval(double range, double value, String msg) {
		Assert.assertTrue(value <= range, msg);
		Assert.assertTrue(value >= -range, msg);
	}
}
