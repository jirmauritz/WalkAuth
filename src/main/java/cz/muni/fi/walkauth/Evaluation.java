package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;

/**
 * Class for computing error of a learned neural network.
 */
public class Evaluation {

	/**
	 * Computes square error function of the neural network on given list of
	 * samples.
	 *
	 * @param neuralNetwork neural network to evaluate, must have single output
	 * neuron
	 * @param samples list of samples on which to calculate the error
	 * @return computed error
	 */
	public static double computeError(NeuralNetwork neuralNetwork, Sample[] samples) {
		int n = samples.length;
		double[] predictedValues = new double[n];
		double[] actualValues = new double[n];
		for (int i=0; i<n; i++) {
			predictedValues[i] = neuralNetwork.computeOutput(Matrix.columnVector(samples[i].getEntries()));
			actualValues[i] = ActivationUtils.labelValue(samples[i]);	
		}

		double error = MathUtils.squareError(predictedValues, actualValues);
		return error;
	}

}
