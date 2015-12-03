package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.DoubleStream;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author Jaroslav Cechak
 */
public class NeuralNetworkLearning {

    private static final Logger logger = Logger.getLogger(NeuralNetworkLearning.class);

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
            // bias doesn't contain any error (-> 0.0)
            errorWrtValues[layersCount] = Matrix.columnVector(new double[]{0.0, output - expectedOutput});
            // go from back to front, but omit the input layer
            for (int l = layersCount - 1; l > 0; l--) {
                // for each neuron from the next layer, multiply derivative wrt.
                // to value with the derivative of activation function to obtain
                // partial derivatives of error wrt. to inner potentials
                Matrix nextNeuronValues = neuralNetwork.getNeuronValuesInLayer(l + 1);
                Matrix errorWrtPotentials = Matrix.zeros(nextNeuronValues.getRowCount(), 1);
                for (int r = 1; r < nextNeuronValues.getRowCount(); r++) {
                    double errorPartial = errorWrtValues[l + 1].get(r, 0)
                            * ActivationUtils.activationFunctionDerivative(nextNeuronValues.get(r, 0));
                    errorWrtPotentials.set(r, 0, errorPartial);
                }

                // compute partial derivatives of error wrt. to values of
                // neurons in current layer
                Matrix neuronValues = neuralNetwork.getNeuronValuesInLayer(l);
                errorWrtValues[l] = Matrix.zeros(neuronValues.getRowCount(), 1);
                for (int j = 1; j < errorWrtValues[l].getRowCount(); j++) {
                    double errorPartial = 0.0;
                    // NOTE: istead of r starting from 1 and then subtracting 1,
                    // it would be better to work with vector without bias (?)
                    for (int r = 1; r < errorWrtPotentials.getRowCount(); r++) {
                        errorPartial += errorWrtPotentials.get(r, 0)
                                * weights[l].get(r - 1, j);
                    }
                    errorWrtValues[l].set(j, 0, errorPartial);
                }

            }

            // and now compute partial derivatives of error wrt. weights
            for (int l = 0; l < layersCount; l++) {
                Matrix oneSampleGradient = new Matrix(weights[l].getRowCount(), weights[l].getColCount());
                for (int j = 0; j < weights[l].getRowCount(); j++) {
                    for (int i = 0; i < weights[l].getColCount(); i++) {
                        double errorPartial = errorWrtValues[l + 1].get(j + 1, 0)
                                * ActivationUtils.activationFunctionDerivative(neuralNetwork.getNeuronValue(l + 1, j + 1))
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
     * Computes normalized gradient of error function.
     *
     * @param neuralNetwork configuration of neural network
     * @param data data for evaluating the error
     * @return partial derivatives of error function with respect to each
     * weight, normalized to a unit vector
     */
    public static Matrix[] normalizedGradient(NeuralNetwork neuralNetwork, Sample[] data) {
        Matrix[] gradient = backpropagation(neuralNetwork, data);

        double squares = 0.0;
        for (int l = 0; l < gradient.length; l++) {
            for (int i = 0; i < gradient[l].getRowCount(); i++) {
                for (int j = 0; j < gradient[l].getColCount(); j++) {
                    double w = gradient[l].get(i, j);
                    squares += w * w;
                }
            }
        }

        if (squares < 0.0001) {
            return gradient;
        }

        double size = Math.sqrt(squares);
        for (int l = 0; l < gradient.length; l++) {
            for (int i = 0; i < gradient[l].getRowCount(); i++) {
                for (int j = 0; j < gradient[l].getColCount(); j++) {
                    double w = gradient[l].get(i, j);
                    gradient[l].set(i, j, w / size);
                }
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
    public static NeuralNetwork gradienDescent(NeuralNetwork neuralNetwork, Sample[] trainingData, Sample[] validationData, double acceptableError, BiFunction<Integer, Double, Double> learningSpeed, int maxIterations) {
        double error;
        int step = 0;
        int numberOfLayers = neuralNetwork.getWeights().length;
        boolean isLearning = true;
        // copy the given neural network
        NeuralNetwork trainedNeuralNetwork = new NeuralNetwork(neuralNetwork.getWeights());
        error = Evaluation.computeError(trainedNeuralNetwork, validationData);

        printHeader();
        log(new String[]{
            Integer.toString(step),
            String.format("%.4f", error),
            String.format("%.4f", Evaluation.computeRMSE(trainedNeuralNetwork, validationData)),
            String.format("%.4f", Evaluation.computeAccuracy(trainedNeuralNetwork, validationData)),
            String.format("%.4f", Evaluation.computePrecision(trainedNeuralNetwork, validationData)),
            String.format("%.4f", Evaluation.computeRecall(trainedNeuralNetwork, validationData)),
            String.format("%.4f", Evaluation.computeF1(trainedNeuralNetwork, validationData)),
            String.format("%.4f", Evaluation.computeError(trainedNeuralNetwork, trainingData)),
            String.format("%.4f", Evaluation.computeRMSE(trainedNeuralNetwork, trainingData)),
            String.format("%.4f", Evaluation.computeAccuracy(trainedNeuralNetwork, trainingData)),
            String.format("%.4f", Evaluation.computePrecision(trainedNeuralNetwork, trainingData)),
            String.format("%.4f", Evaluation.computeRecall(trainedNeuralNetwork, trainingData)),
            String.format("%.4f", Evaluation.computeF1(trainedNeuralNetwork, trainingData))
        });

        while (error > acceptableError && step < maxIterations && isLearning) {
            step++;
            Matrix[] newLayers = new Matrix[numberOfLayers];
            Matrix[] errorDerivationsByWeight = normalizedGradient(trainedNeuralNetwork, trainingData);

            for (int i = 0; i < numberOfLayers; i++) {
                double speed = learningSpeed.apply(step, error);
                Matrix errorDerivationByWeight = errorDerivationsByWeight[i];
                // subtract gradient times speed to the original weights
                newLayers[i] = trainedNeuralNetwork.getWeights()[i].add(errorDerivationByWeight.multiplyByScalar(-1 * speed)); /*((error < 100) ? (error / trainingData.length) : 1)*/

            }
            isLearning = !Arrays.equals(newLayers, trainedNeuralNetwork.getWeights());

            trainedNeuralNetwork.setWeights(newLayers);
            error = Evaluation.computeError(trainedNeuralNetwork, validationData);

            log(new String[]{
                Integer.toString(step),
                String.format("%.4f", error),
                String.format("%.4f", Evaluation.computeRMSE(trainedNeuralNetwork, validationData)),
                String.format("%.4f", Evaluation.computeAccuracy(trainedNeuralNetwork, validationData)),
                String.format("%.4f", Evaluation.computePrecision(trainedNeuralNetwork, validationData)),
                String.format("%.4f", Evaluation.computeRecall(trainedNeuralNetwork, validationData)),
                String.format("%.4f", Evaluation.computeF1(trainedNeuralNetwork, validationData)),
                String.format("%.4f", Evaluation.computeError(trainedNeuralNetwork, trainingData)),
                String.format("%.4f", Evaluation.computeRMSE(trainedNeuralNetwork, trainingData)),
                String.format("%.4f", Evaluation.computeAccuracy(trainedNeuralNetwork, trainingData)),
                String.format("%.4f", Evaluation.computePrecision(trainedNeuralNetwork, trainingData)),
                String.format("%.4f", Evaluation.computeRecall(trainedNeuralNetwork, trainingData)),
                String.format("%.4f", Evaluation.computeF1(trainedNeuralNetwork, trainingData))
            });
        }

        if (error <= acceptableError) {
            System.out.println("Gradient descent finnished with error being lower than acceptable error.");
        } else if (step == maxIterations) {
            System.out.println("Gradient descent finnished due to exceeding " + maxIterations + " iterations.");
        } else {
            System.out.println("Gradient descent finnished due network not learning anything.");
        }
        return trainedNeuralNetwork;
    }

    private static void printHeader() {
        // rollover file so that new csv file is created
        ((RollingFileAppender) logger.getAppender("csv.file")).rollOver();
        // print header of csv
        logger.info("iteration, validation error, validation RMSE, validation accuracy, validation precision, validation recall, validation F1, training error, training RMSE, training accuracy, training precision, training recall, training F1");

    }

    private static void log(String[] message) {
        // concatenate all the information into one line separted with commas
        StringBuilder sb = new StringBuilder();
        if (message.length >= 1) {
            sb.append(message[0]);
        }
        for (int i = 1; i < message.length; i++) {
            sb.append(", ").append(message[i]);
        }

        logger.info(sb.toString());
    }

    /**
     * Method initialize weights of given network.
     *
     * The initialization is computed as stated in slides of PV021, i.e. weights
     * are initialized randomly in interval [-w,w] where w = sqrt(3/d) where d
     * is number of weights pointing in the same neuron as computed weight.
     *
     * @param network to be assigned initialized weights
     * @return new instance of NeuralNetwork
     */
    public static NeuralNetwork initializeWeights(NeuralNetwork network) {
        int[] neurons = network.getLayers();
        Matrix[] newWeights = new Matrix[neurons.length - 1]; // weight are between neurons -> -1
        for (int i = 0; i < neurons.length - 1; i++) {
            int numOfLowNeurons = neurons[i];
            int numOfHighNeurons = neurons[i + 1];
            double[][] weightsInLayer = new double[numOfHighNeurons][numOfLowNeurons + 1];
            for (int row = 0; row < numOfHighNeurons; row++) {
                // values
                for (int col = 0; col < numOfLowNeurons + 1; col++) {
                    weightsInLayer[row][col] = computeInitWeight(numOfLowNeurons);
                }
            }

            newWeights[i] = new Matrix(weightsInLayer);
        }
        return new NeuralNetwork(newWeights);
    }

    /**
     * Computes random double in the interval [-w,w] where w = sqrt(3/d) where d
     * is number of weights pointing in the same neuron as computed weight.
     *
     * @param d is number of neurons on the lower layer
     */
    private static double computeInitWeight(double d) {
        // compute range value w
        double rangeValue = Math.sqrt(3 / d);

        // compute random value r in [0,1]
        Random rand = new Random(System.nanoTime());
        DoubleStream ds = rand.doubles();
        double randomDouble = ds.findAny().getAsDouble();

        // make it range [-1,1]
        randomDouble = (randomDouble - 0.5) * 2;

        // expand value to interal [-w,w]
        return randomDouble * rangeValue;
    }

    /**
     * This method trains completly new neural network with given topology on
     * given data.
     *
     * @param networkTopology array of integers that denotes the number of
     * neurons in each layer
     * @param trainingData array of training inputs
     * @param validationData array of valdation inputs
     * @param acceptableError maximal acceptable error
     * @param learningSpeed function that for the given number of passes returns
     * learning speed (epsilon from slides)
     * @param maxIterations limit for iterations
     * @return Returns trained neural network
     */
    public static NeuralNetwork trainNeuralNetwork(int[] networkTopology, Sample[] trainingData, Sample[] validationData, double acceptableError, BiFunction<Integer, Double, Double> learningSpeed, int maxIterations) {
        NeuralNetwork empty = new NeuralNetwork(networkTopology);
        //System.out.println("New neural network has been created." + empty);
        NeuralNetwork randomlyInitializedNetwork = initializeWeights(empty);
        //System.out.println("Weights has been randomly inicialized." + randomlyInitializedNetwork);
        return gradienDescent(randomlyInitializedNetwork, trainingData, validationData, acceptableError, learningSpeed, maxIterations);
    }

}
