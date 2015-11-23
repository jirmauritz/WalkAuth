package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.List;
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
    public static List<Matrix> backpropagation(NeuralNetwork neuralNetwork, List<Sample> data) {
        return null;
    }

    /**
     * Gradient descent algorithm for neural network training.
     *
     * @param neuralNetwork neural network that is to be trained
     * @param trainingData array of training input vectors
     * @param trainingOutputs array of training output vectors
     * @param validationData array of valdation input vectors
     * @param validationOutput array of validation output vectors
     * @param acceptableError maximal acceptable error
     * @param learningSpeed function that for the given number of passes returns
     * learning speed (epsilon from slides)
     * @return
     */
    public static NeuralNetwork gradienDescent(NeuralNetwork neuralNetwork, Matrix[] trainingData, Matrix[] trainingOutputs, Matrix[] validationData, Matrix[] validationOutput, double acceptableError, Function<Integer, Double> learningSpeed) {
        double error = 0;
        int step = 0;
        int numberOfLayers = neuralNetwork.getLayers().length;

        NeuralNetwork trainedNeuralNetwork = new NeuralNetwork(neuralNetwork.getLayers());

        for (int i = 0; i < trainingData.length; i++) {
            error += computeError(trainedNeuralNetwork, trainingData[i], trainingOutputs[i]);
        }

        while (error > acceptableError) {
            error = 0;
            step++;

            Matrix[] newLayers = new Matrix[numberOfLayers];
            List<Matrix> derivations = backpropagation(trainedNeuralNetwork, null);

            for (int i = 0; i < numberOfLayers; i++) {
                double speed = learningSpeed.apply(step);
                Matrix derivation = derivations.get(i);
                Matrix m = trainedNeuralNetwork.getLayers()[i].add(derivation.multiplyByScalar(speed));
                newLayers[i] = m;
            }

            trainedNeuralNetwork.setLayers(newLayers);

            for (int i = 0; i < trainingData.length; i++) {
                error += computeError(trainedNeuralNetwork, trainingData[i], trainingOutputs[i]);
            }
        }

        return trainedNeuralNetwork;
    }

    private static double computeError(NeuralNetwork neuralNetwork, Matrix data, Matrix output) {
        Matrix result = neuralNetwork.computeOutputs(data);

        double[] expected = new double[result.getRowCount()];
        double[] real = new double[result.getRowCount()];

        for (int j = 0; j < result.getRowCount(); j++) {
            expected[j] = output.get(j, 1);
            real[j] = result.get(j, 1);
        }

        return MathUtils.squareError(expected, real);

    }

}
