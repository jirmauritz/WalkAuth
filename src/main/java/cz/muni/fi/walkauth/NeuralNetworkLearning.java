package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.LinkedList;
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

    public static NeuralNetwork gradienDescent(NeuralNetwork neuralNetwork, List<Matrix> trainingData, List<Matrix> trainingOutputs, List<Matrix> validationData, List<Matrix> validationOutput, double acceptableError, Function<Integer, Double> learningSpeed) {
        double error = 0;
        int step = 0; 
        
        NeuralNetwork trainedNeuralNetwork = new NeuralNetwork(neuralNetwork.getLayers());

        for (int i = 0; i < trainingData.size(); i++) {
            error += computeError(trainedNeuralNetwork, trainingData.get(i), trainingOutputs.get(i));
        }

        while (error > acceptableError) {
            error = 0;
            step++;

            List<Matrix> newLayers = new LinkedList<>();
            List<Matrix> derivations = backpropagation(trainedNeuralNetwork, null);
            
            for (int i = 0; i < trainedNeuralNetwork.getLayers().size(); i++) {
                double speed = learningSpeed.apply(step);
                Matrix derivation = derivations.get(i);
                Matrix m = trainedNeuralNetwork.getLayers().get(i).add(derivation.multiplyByScalar(speed));
                newLayers.add(m);
            }
            
            trainedNeuralNetwork.setLayers(newLayers);

            for (int i = 0; i < trainingData.size(); i++) {
                error += computeError(trainedNeuralNetwork, trainingData.get(i), trainingOutputs.get(i));
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
