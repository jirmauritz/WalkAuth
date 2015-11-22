package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for backpropagation algorithm.
 */
public class BackpropagationTest {
	

	@Before
	public void setUp() {
	}
	
	/**
	 * Test of backpropagation for a no sample.
	 */
	@Test
	public void test_no_sample() {
		NeuralNetwork neuralNetwork = new NeuralNetwork(1, 1);
		List<Sample> data = new ArrayList<>();
		List<Matrix> gradient = NeuralNetworkLearning.backpropagation(neuralNetwork, data);
		double[][] g1 = {{0.0}, {0.0}};
		assertEquals(Arrays.asList(new Matrix(g1)), gradient);
	}
	
	/**
	 * Test of backpropagation for a single sample and 1-1 network architecture.
	 */
	@Test
	public void test_single_sample_1_1_architecture() {
		NeuralNetwork neuralNetwork = new NeuralNetwork(1, 1);
		List<Sample> data = null;//Arrays.asList(Sample([1], true));
		List<Matrix> gradient = NeuralNetworkLearning.backpropagation(neuralNetwork, data);
		//double[][] g1 = {{0.0}, {0.0}};
		assertEquals(Arrays.asList(new Matrix(new double[][] {{0.0}, {0.0}})), gradient);
	}
	
}

