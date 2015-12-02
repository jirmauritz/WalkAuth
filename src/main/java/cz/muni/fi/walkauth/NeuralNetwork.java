package cz.muni.fi.walkauth;

import java.util.Arrays;

/**
 * Class that represents neural network. It can have various number of neurons
 * as well as layers.
 *
 * @author Jaroslav Cechak
 */
public class NeuralNetwork {

    // weights between neurons (1 matrix of weights between adjancent layers
    private Matrix[] weights;

    // values of neurons after the last computation (column vector for each layer)
    private Matrix[] neuronValues;

    // some useful instances
    public static final NeuralNetwork IDENTITY = new NeuralNetwork(
            new Matrix[]{
                new Matrix(new double[][]{{0.0, 1.0}})
            }
    );

//    /**
//     * Creates a neural network with the given layer layout and random weights
//     * between 0 and 1. Every neuron is conneted to every other.
//     *
//     * @param layers sequence of integers corresponding to layer sizes (e.g. 2 3
//     * 2)
//     */
//    public NeuralNetwork(int... layers) {
//        this.layers = new Matrix[layers.length - 1];
//        // size of previous layer
//        int previous = 0;
//
//        for (int i = 0; i < layers.length; i++) {
//            if (previous != 0) {
//                // add layer, +1 for bias
//                this.layers[i] = Matrix.random(layers[i], previous + 1);
//            }
//            previous = layers[i];
//        }
//    }
    /**
     * Creates a neural network with the given layer counts. The weights are not
     * initialized.
     *
     * @param layerSizes size of each layer, including the input one
     */
    public NeuralNetwork(int... layerSizes) {
        this.neuronValues = new Matrix[layerSizes.length];
        this.weights = new Matrix[layerSizes.length - 1];
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = new Matrix(layerSizes[i + 1], layerSizes[i] + 1);
        }
    }

    public NeuralNetwork(Matrix[] weights) {
        if (weights == null) {
            throw new IllegalArgumentException("List of layer cannot be null.");
        }
        if (weights.length < 1) {
            throw new IllegalArgumentException("There must be atleas one layer of neurons.");
        }
        for (int i = 1; i < weights.length; i++) {
            // -1 for bias
            if (weights[i].getColCount() - 1 != weights[i - 1].getRowCount()) {
                throw new IllegalArgumentException("Not valid layers, matrix dimensions do not corresponds.");
            }
        }

        this.weights = Arrays.copyOf(weights, weights.length);
        this.neuronValues = new Matrix[weights.length + 1];
    }

    public Matrix[] getWeights() {
        return Arrays.copyOf(this.weights, this.weights.length);
    }

    public void setWeights(Matrix[] layers) {
        for (int i = 1; i < layers.length; i++) {
            // -1 for bias
            if (layers[i].getColCount() - 1 != layers[i - 1].getRowCount()) {
                throw new IllegalArgumentException("Not valid layers, matrix dimensions do not corresponds.");
            }
        }

        this.weights = Arrays.copyOf(layers, layers.length);
    }

    public int[] getLayers() {
        int[] layers = new int[weights.length + 1];
        for (int i = 0; i < weights.length; i++) {
            layers[i] = weights[i].getColCount() - 1;
        }
        layers[weights.length] = weights[weights.length - 1].getRowCount();
        return layers;
    }

    /**
     * Get one layer of neuron values
     *
     * @param layer layer order number (input layer is 0)
     * @return column vector of values of neurons in this layer
     */
    public Matrix getNeuronValuesInLayer(int layer) {
        return neuronValues[layer];
    }

    public double getNeuronValue(int layer, int row) {
        return neuronValues[layer].get(row, 0);
    }

    /**
     * Gets the weight of given neuron.
     *
     * @param layer layer in which neuron is
     * @param neuronNumber position in layer
     * @param weightNumber position in neuron
     * @return weight of given neuron
     */
    public double getNeuronWeight(int layer, int neuronNumber, int weightNumber) {
        if (layer == 0) {
            throw new IllegalArgumentException("Input neurons do not have weights.");
        } else {
            return weights[layer - 1].get(neuronNumber, weightNumber);
        }
    }

    /**
     * Sets the weight of given neuron.
     *
     * @param layer layer in which neuron is
     * @param neuronNumber position in layer
     * @param weightNumber position in neuron
     * @param weight weight to insert
     *
     * public void setNeuronWeight(int layer, int neuronNumber, int
     * weightNumber, double weight) { if (layer == 0) { throw new
     * IllegalArgumentException("Input neurons do not have weights."); } else {
     * layers.get(layer - 1).set(neuronNumber, weightNumber, weight); } }
     */
    /**
     * Computes output values for the given input values.
     *
     * @param inputs input values
     * @return output values of output neurons
     */
    public Matrix computeOutputs(Matrix inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        // -1 for bias
        if (inputs.getColCount() != 1 || inputs.getRowCount() != weights[0].getColCount() - 1) {
            throw new IllegalArgumentException("Input matrix does not have required dimensions. "
                    + "Got " + inputs.getRowCount() + "x" + inputs.getColCount()
                    + " but expected " + (weights[0].getColCount() - 1) + "x1.");
        }

        Matrix values = inputs;
        neuronValues[0] = addBias(inputs);
        for (int l = 1; l <= weights.length; l++) {
            // sum all inputs and apply activation function
            Matrix potentials = weights[l - 1].multiply(neuronValues[l - 1]);
            values = potentialsToOutputs(potentials);

            // add bias to all layers -- including the last one (for consistency)
            neuronValues[l] = addBias(values);
            //// only add bias if it's not an output layer
            //neuronValues[l] = (l == weights.length) ? values : addBias(values);
        }

        return values; //neuronValues[neuronValues.length - 1];
    }

    /**
     * Computes output value in case there is only one output neuron.
     *
     * @param inputs vector of input values
     * @return output of the only output neuron
     */
    public double computeOutput(Matrix inputs) {
        if (weights[weights.length - 1].getRowCount() != 1) {
            throw new UnsupportedOperationException("This neural network has more the one output neuron.");
        }
        Matrix result = computeOutputs(inputs);
        return result.get(0, 0);
    }

    /**
     * Adds bias to input for the next layer as the first item of vector
     *
     * @param input input to be modified
     * @return input with bais as the first element of the vector
     */
    private static Matrix addBias(Matrix input) {
        double[][] newInput = new double[input.getRowCount() + 1][1];
        newInput[0][0] = 1;

        for (int i = 0; i < input.getRowCount(); i++) {
            newInput[i + 1][0] = input.getValues()[i][0];
        }

        return new Matrix(newInput);
    }

    /**
     * Applies activation function to potential of every neuron.
     *
     * @param potentials potentials of neurons
     * @return matrix with neurons' outputs
     */
    private static Matrix potentialsToOutputs(Matrix potentials) {
        if (potentials == null) {
            throw new IllegalArgumentException("Potetial matrix cannot be null.");
        }

        for (int i = 0; i < potentials.getRowCount(); i++) {
            double potential = potentials.get(i, 0);
            potentials.set(i, 0, ActivationUtils.activationFunction(potential));
        }

        return potentials;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NeuralNetwork with topology topology ");

        sb.append(weights[0].getColCount());

        for (Matrix layer : weights) {
            sb.append("-");
            sb.append(layer.getRowCount());
        }

        for (Matrix layer : weights) {
            sb.append("\n");
            sb.append(layer.toString());
        }

        return sb.toString();
    }
}
