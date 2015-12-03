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
        for (int i = 0; i < n; i++) {
            predictedValues[i] = neuralNetwork.computeOutput(Matrix.columnVector(samples[i].getEntries()));
            actualValues[i] = ActivationUtils.labelValue(samples[i]);
        }

        double error = MathUtils.squareError(predictedValues, actualValues);
        return error;
    }

    /**
     * Computes root mean square error of a trained neural network for given
     * testing test.
     * 
     * @param neuralNetwork neural network to evaluate
     * @param samples testing data set
     * @return RMSE
     */
    public static double computeRMSE(NeuralNetwork neuralNetwork, Sample[] samples) {
        int n = samples.length;
        double[] predictedValues = new double[n];
        double[] actualValues = new double[n];
        for (int i = 0; i < n; i++) {
            predictedValues[i] = neuralNetwork.computeOutput(Matrix.columnVector(samples[i].getEntries()));
            actualValues[i] = ActivationUtils.labelValue(samples[i]);
        }

        double error = MathUtils.rmse(predictedValues, actualValues);
        return error;
    }

    /**
     * Computes accuracy of a trained neural network for given testing test.
     * 
     * @param neuralNetwork neural network to evaluate
     * @param samples testing data set
     * @return accuracy
     */
    public static double computeAccuracy(NeuralNetwork neuralNetwork, Sample[] samples) {
        int n = samples.length;

        if (n == 0) {
            return 0.0;
        }

        int correctCount = 0;
        for (int i = 0; i < n; i++) {
            boolean predictedLabel = neuralNetwork.computeOutput(Matrix.columnVector(samples[i].getEntries())) >= 0;
            boolean correct = predictedLabel == samples[i].isPositiveUserData();
            correctCount += correct ? 1 : 0;
        }

        double accuracy = (float) correctCount / n;
        return accuracy;
    }

    /**
     * Computes precision of a trained neural network for given testing test.
     * Precision = true positives / all positives (positive = marked as
     * positive).
     * 
     * @param neuralNetwork neural network to evaluate
     * @param samples testing data set
     * @return precision
     */
    public static double computePrecision(NeuralNetwork neuralNetwork, Sample[] samples) {
        int truePositives = 0;
        int allPositives = 0;
        for (int i = 0; i < samples.length; i++) {
            boolean predictedLabel = neuralNetwork.computeOutput(Matrix.columnVector(samples[i].getEntries())) >= 0;
            if (predictedLabel) {
                allPositives += 1;
                if (predictedLabel == samples[i].isPositiveUserData()) {
                    truePositives += 1;
                }                
            }  
        }
        
        if (allPositives == 0) {
            return 1.0;
        }

        double precision = (float) truePositives / allPositives;
        return precision;
    }
 
    /**
     * Computes recall of a trained neural network for given testing test.
     * Recall = true positives / (true positives + false negatives)
     * (positive = marked as positive).
     * 
     * @param neuralNetwork neural network to evaluate
     * @param samples testing data set
     * @return recall
     */
    public static double computeRecall(NeuralNetwork neuralNetwork, Sample[] samples) {
        int truePositives = 0;
        int allTrue = 0;
        for (int i = 0; i < samples.length; i++) {
            boolean trueLabel = samples[i].isPositiveUserData();
            if (trueLabel) {
                allTrue += 1;
                double output = neuralNetwork.computeOutput(Matrix.columnVector(samples[i].getEntries()));
                boolean predictedLabel = output >= 0;
                if (predictedLabel) {
                    truePositives += 1;
                }                
            }
        }
        
        if (allTrue == 0) {
            return 1.0;
        }

        double precision = (float) truePositives / allTrue;
        return precision;
    }    
     
    /**
     * Computes F1 score of a trained neural network for given testing test.
     * F1= 2 * precision * recall / (precision + recall),
     * which is a harmonic mean of precision and recall.
     * 
     * @param neuralNetwork neural network to evaluate
     * @param samples testing data set
     * @return F1 score
     */
    public static double computeF1(NeuralNetwork neuralNetwork, Sample[] samples) {
        double precision = computePrecision(neuralNetwork, samples);
        double recall = computeRecall(neuralNetwork, samples);
        
        if (precision + recall == 0) {
            return 0.0;
        }
        
        double f1 = 2 * precision * recall / (precision + recall);
        return f1;
    }

}
