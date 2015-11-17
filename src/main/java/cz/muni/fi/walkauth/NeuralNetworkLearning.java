package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.List;

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
    
    public static NeuralNetwork gradienDescent(NeuralNetwork n, List<Sample> trainingData, List<Sample> validationData) {
        return null;
    }

}
