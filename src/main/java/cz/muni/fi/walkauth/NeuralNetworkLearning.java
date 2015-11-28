package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.function.Function;

/**
 *
 * @author Jaroslav Cechak
 */
public class NeuralNetworkLearning {

	/**
	 * Backpropagation algorithm for computing gradient of error function.
	 *
	 * @param neuralNetwork configuration of neural network
	 * @param data data for evaluating the error
	 * @return partial derivatives of error function with respect to each weight
	 */
	public static Matrix[] backpropagation(NeuralNetwork neuralNetwork, Sample[] data) {
		Matrix[] weights = neuralNetwork.getWeights();
		int layersCount = weights.length;

		// initialize gradient to zero
		Matrix[] gradient = new Matrix[layersCount];
		for (int i = 0; i < layersCount; i++) {
			gradient[i] = Matrix.zeros(weights[i].getRowCount(), weights[i].getColCount());
		}

		for (Sample sample : data) {
			// compute partial derivatives of error wrt. neuron values
			Matrix[] errorWrtValues = new Matrix[layersCount + 1];
			double output = neuralNetwork.computeOutput(Matrix.columnVector(sample.getEntries()));
			double expectedOutput = ActivationUtils.labelValue(sample);
			errorWrtValues[layersCount] = Matrix.columnVector(new double[] {output - expectedOutput});
			// go from back to front, but omit the input layer
			for (int l = layersCount - 1; l > 0; l--) {
				// for each neuron from the next layer, mulitply derivative wrt.
				// to value with the derivative of activation function to obtain
				// partial derivatives of error wrt. to inner potentials
				Matrix nextNeuronValues = neuralNetwork.getNeuronValuesInLayer(l + 1);
				Matrix errorWrtPotentials = new Matrix(nextNeuronValues.getRowCount(), 1);
				for (int r = 0; r < nextNeuronValues.getRowCount(); r++) {
					double errorPartial = errorWrtValues[l + 1].get(r, 0)
						* ActivationUtils.activationFunctionDerivative(nextNeuronValues.get(r, 0));
					errorWrtPotentials.set(r, 0, errorPartial);
				}				
				
				// compute partial derivatives of error wrt. to values of
				// neurons in current layer
				Matrix neuronValues = neuralNetwork.getNeuronValuesInLayer(l);
				errorWrtValues[l] = Matrix.zeros(neuronValues.getRowCount(), 1);
				for (int j = 0; j < neuronValues.getRowCount(); j++) {
					double errorPartial = 0;
					for (int r = 0; r < errorWrtPotentials.getRowCount(); r++) {
						errorPartial += errorWrtPotentials.get(r, 0)
							* weights[l].get(r, j);
					}					
					errorWrtValues[l].set(j, 0, errorPartial);					
				}
			}
			
			// and now compute partial derivatives of error wrt. weights
			for (int l = 0; l < layersCount; l++) {
				Matrix oneSampleGradient = new Matrix(weights[l].getRowCount(), weights[l].getColCount());
				for (int j = 0; j < weights[l].getRowCount(); j++) {
					for (int i = 0; i < weights[l].getColCount(); i++) {						
						double errorPartial = errorWrtValues[l + 1].get(j, 0)
							* ActivationUtils.activationFunctionDerivative(neuralNetwork.getNeuronValue(l + 1, j))
							* neuralNetwork.getNeuronValue(l, i);
						oneSampleGradient.set(j, i, errorPartial);
					}
				}
				// add gradient for the current sample to the total gradient
				gradient[l] = gradient[l].add(oneSampleGradient);
			}
		}

		return gradient;
	}

    /**
     * Gradient descent algorithm for neural network training.
     *
     * @param neuralNetwork neural network that is to be trained
     * @param trainingData array of training inputs
     * @param validationData array of valdation inputs
     * @param acceptableError maximal acceptable error
     * @param learningSpeed function that for the given number of passes returns
     * learning speed (epsilon from slides)
     * @return trained neural network
     */
    public static NeuralNetwork gradienDescent(NeuralNetwork neuralNetwork, Sample[] trainingData, Sample[] validationData, double acceptableError, Function<Integer, Double> learningSpeed) {
        double error = 0;
        int step = 0;
        int numberOfLayers = neuralNetwork.getWeights().length;

        // copy the given neural network
        NeuralNetwork trainedNeuralNetwork = new NeuralNetwork(neuralNetwork.getWeights());

        error = computeError(trainedNeuralNetwork, validationData);

        while (error > acceptableError) {
            step++;

            Matrix[] newLayers = new Matrix[numberOfLayers];
            Matrix[] errorDerivationsByWeight = backpropagation(trainedNeuralNetwork, trainingData);

            for (int i = 0; i < numberOfLayers; i++) {
                double speed = learningSpeed.apply(step);
                Matrix errorDerivationByWeight = errorDerivationsByWeight[i];
                // subtract gradient times speed to the original weights
                newLayers[i] = trainedNeuralNetwork.getWeights()[i].add(errorDerivationByWeight.multiplyByScalar(-1 * speed));
            }

            trainedNeuralNetwork.setWeights(newLayers);

            error = computeError(trainedNeuralNetwork, validationData);
        }

        return trainedNeuralNetwork;
    }

    /**
     * Function that computes combined error over given test samples.
     *
     * @param neuralNetwork network which error will be computed
     * @param data array of sample data
     * @return computed error for given neural network and given data
     */
    public static double computeError(NeuralNetwork neuralNetwork, Sample[] data) {
        double[] predicted = new double[data.length];
        double[] actual = new double[data.length];
        double error = 0;

        for (int i = 0; i < data.length; i++) {
            predicted[i] = ActivationUtils.labelValue(data[i]);
            actual[i] = neuralNetwork.computeOutput(Matrix.columnVector(data[i].getEntries()));
        }

        error += MathUtils.squareError(predicted, actual);

        return error;
    }

}
