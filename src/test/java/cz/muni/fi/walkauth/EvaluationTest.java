package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import static org.mockito.Matchers.any;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for neural network evaluation.
 */
public class EvaluationTest {

	private static final double EPSILON = 0.001;
	
	@Mock
	private NeuralNetwork positiveNeuralNetwork;
	
	@Mock
	private NeuralNetwork halfPositiveNeuralNetwork;
	
	@Mock
	private NeuralNetwork neutralNeuralNetwork;
	
	@Mock
	private NeuralNetwork negativeNeuralNetwork;
	
	private final double[] ANY_INPUT = new double[]{0};
	private final Sample[] onePositiveSample = {new Sample(true, ANY_INPUT)};
	private final Sample[] oneNegativeSample = {new Sample(false, ANY_INPUT)};

	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(positiveNeuralNetwork.computeOutput(any(Matrix.class))).thenReturn(1.0);
		when(halfPositiveNeuralNetwork.computeOutput(any(Matrix.class))).thenReturn(0.5);
		when(neutralNeuralNetwork.computeOutput(any(Matrix.class))).thenReturn(0.0);
		when(negativeNeuralNetwork.computeOutput(any(Matrix.class))).thenReturn(-1.0);
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
		Sample[] samples = {new Sample(true, ANY_INPUT)};
		double result = Evaluation.computeError(positiveNeuralNetwork, samples);
		assertEquals(result, 0.0, EPSILON);
	}

	/**
	 * Error computed from true negatives should be 0.
	 */
	@Test
	public void testComputeErrorTrueNegative() {
		Sample[] samples = {new Sample(false, ANY_INPUT)};
		double result = Evaluation.computeError(negativeNeuralNetwork, samples);
		assertEquals(result, 0.0, EPSILON);
	}

	/**
	 * Errors computed from false positive and false negative should be same and
	 * greater than 0.
	 */
	@Test
	public void testComputeErrorFalsePositiveNegativeGreaterThanZero() {
		NeuralNetwork neuralNetwork = NeuralNetwork.IDENTITY;
		Sample[] samples1 = {new Sample(false, ANY_INPUT)};
		double result1 = Evaluation.computeError(neuralNetwork, samples1);
		Sample[] samples2 = {new Sample(true, ANY_INPUT)};
		double result2 = Evaluation.computeError(neuralNetwork, samples2);
		assertEquals(result1, result2, EPSILON);
		assertTrue(result1 > 0.0 + EPSILON);
	}

	/**
	 * Error function should be monotonic with respect to one sample value.
	 */
	@Test
	public void testComputeErrorMonoticity() {
		double result1 = Evaluation.computeError(positiveNeuralNetwork, onePositiveSample);
		double result2 = Evaluation.computeError(halfPositiveNeuralNetwork, onePositiveSample);
		double result3 = Evaluation.computeError(neutralNeuralNetwork, onePositiveSample);
		double result4 = Evaluation.computeError(negativeNeuralNetwork, onePositiveSample);

		assertTrue(result1 < result2 && result2 < result3 && result3 < result4);

		double result5 = Evaluation.computeError(negativeNeuralNetwork, oneNegativeSample);
		double result6 = Evaluation.computeError(neutralNeuralNetwork, oneNegativeSample);
		double result7 = Evaluation.computeError(halfPositiveNeuralNetwork, oneNegativeSample);
		double result8 = Evaluation.computeError(positiveNeuralNetwork, oneNegativeSample);

		assertTrue(result5 < result6 && result6 < result7 && result7 < result8);
	}

	/**
	 * Test that error function return half of a square of distance between
	 * computed and expected result.
	 */
	@Test
	public void testComputeErrorSquareValues() {				
		double result1 = Evaluation.computeError(halfPositiveNeuralNetwork, onePositiveSample);
		assertEquals(result1, 0.125, EPSILON);
		
		double result2 = Evaluation.computeError(neutralNeuralNetwork, onePositiveSample);
		assertEquals(result2, 0.5, EPSILON);
		
		double result3 = Evaluation.computeError(negativeNeuralNetwork, onePositiveSample);
		assertEquals(result3, 2.0, EPSILON);
		
		double result4 = Evaluation.computeError(neutralNeuralNetwork, oneNegativeSample);
		assertEquals(result4, 0.5, EPSILON);
		
		double result5 = Evaluation.computeError(halfPositiveNeuralNetwork, oneNegativeSample);
		assertEquals(result5, 1.125, EPSILON);
		
		double result6 = Evaluation.computeError(positiveNeuralNetwork, oneNegativeSample);
		assertEquals(result6, 2.0, EPSILON);
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
