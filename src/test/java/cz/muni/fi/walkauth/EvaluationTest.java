package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for neural network evaluation.
 */
public class EvaluationTest {

	private static final double EPSILON = 0.001;

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Error computed from no sample should be always 0.
	 */
	@Test
	public void testComputeErrorNoSamples() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;
		Sample[] samples = {};
		double result = Evaluation.computeError(neuralNetwork, samples);
		double expResult = 0.0;
		assertEquals(expResult, result, EPSILON);
	}

	/**
	 * Error computed from true positives should be 0.
	 */
	@Test
	public void testComputeErrorTruePositive() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;
		Sample[] samples = new Sample[]{new Sample(true, new double[]{ActivationUtils.AMPLITUDE})};
		double result = Evaluation.computeError(neuralNetwork, samples);
		assertEquals(0.0, result, EPSILON);
	}

	/**
	 * Error computed from true negatives should be 0.
	 */
	@Test
	public void testComputeErrorTrueNegative() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;
		Sample[] samples = new Sample[]{new Sample(false, new double[]{-ActivationUtils.AMPLITUDE})};
		double result = Evaluation.computeError(neuralNetwork, samples);
		assertEquals(0.0, result, EPSILON);
	}

	/**
	 * Errors computed from false positive and false negative should be same and
	 * greater than 0.
	 */
	@Test
	public void testComputeErrorFalsePositive() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;
		Sample[] samples1 = new Sample[]{new Sample(false, new double[]{ActivationUtils.AMPLITUDE})};
		double result1 = Evaluation.computeError(neuralNetwork, samples1);
		Sample[] samples2 = new Sample[]{new Sample(true, new double[]{-ActivationUtils.AMPLITUDE})};
		double result2 = Evaluation.computeError(neuralNetwork, samples2);
		assertEquals(result1, result2, EPSILON);
		assertTrue(result1 > 0.0 + EPSILON);
	}

	/**
	 * Error function should be monotonic with respect to one sample value.
	 */
	@Test
	public void testComputeErrorMonoticity() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;

		Sample[] samples1 = new Sample[]{new Sample(true, new double[]{ActivationUtils.AMPLITUDE})};
		Sample[] samples2 = new Sample[]{new Sample(true, new double[]{0.5 * ActivationUtils.AMPLITUDE})};
		Sample[] samples3 = new Sample[]{new Sample(true, new double[]{0.0})};
		Sample[] samples4 = new Sample[]{new Sample(true, new double[]{-0.5 * ActivationUtils.AMPLITUDE})};

		double result1 = Evaluation.computeError(neuralNetwork, samples1);
		double result2 = Evaluation.computeError(neuralNetwork, samples2);
		double result3 = Evaluation.computeError(neuralNetwork, samples3);
		double result4 = Evaluation.computeError(neuralNetwork, samples4);

		assertTrue(result1 < result2 && result2 < result3 && result3 < result4);

		Sample[] samples5 = new Sample[]{new Sample(false, new double[]{-ActivationUtils.AMPLITUDE})};
		Sample[] samples6 = new Sample[]{new Sample(false, new double[]{-0.5 * ActivationUtils.AMPLITUDE})};
		Sample[] samples7 = new Sample[]{new Sample(false, new double[]{0.0})};
		Sample[] samples8 = new Sample[]{new Sample(false, new double[]{0.5 * ActivationUtils.AMPLITUDE})};

		double result5 = Evaluation.computeError(neuralNetwork, samples5);
		double result6 = Evaluation.computeError(neuralNetwork, samples6);
		double result7 = Evaluation.computeError(neuralNetwork, samples7);
		double result8 = Evaluation.computeError(neuralNetwork, samples8);

		assertTrue(result5 < result6 && result6 < result7 && result7 < result8);
	}

	/**
	 * Test that error function return half of a square of distance between
	 * computed and expected result.
	 */
	@Test
	public void testComputeErrorSquareValues() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;

		Sample[] samples1 = new Sample[]{new Sample(true, new double[]{ActivationUtils.AMPLITUDE - 1})};
		double result1 = Evaluation.computeError(neuralNetwork, samples1);
		assertEquals(0.5, result1, EPSILON);

		Sample[] samples2 = new Sample[]{new Sample(true, new double[]{ActivationUtils.AMPLITUDE - 2})};
		double result2 = Evaluation.computeError(neuralNetwork, samples2);
		assertEquals(2.0, result2, EPSILON);

		Sample[] samples3 = new Sample[]{new Sample(false, new double[]{-ActivationUtils.AMPLITUDE + 3})};
		double result3 = Evaluation.computeError(neuralNetwork, samples3);
		assertEquals(4.5, result3, EPSILON);
	}

	/**
	 * Test that error function has additive behavior with respect to the number
	 * of samples.
	 */
	@Test
	public void testComputeErrorAdditivity() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;
		
		Sample sample1 = new Sample(true, new double[]{0.7});
		Sample sample2 = new Sample(false, new double[]{0.2});

		double result1 = Evaluation.computeError(neuralNetwork, new Sample[]{sample1});
		double result2 = Evaluation.computeError(neuralNetwork, new Sample[]{sample2});
		double result3 = Evaluation.computeError(neuralNetwork, new Sample[]{sample1, sample2});
		
		assertEquals(result1 + result2, result3, EPSILON);
	}
}
