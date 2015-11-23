package cz.muni.fi.walkauth;

import java.util.Arrays;

/**
 * Class that represents neural network. It can have various number of neurons
 * as well as layers.
 *
 * @author Jaroslav Cechak
 */
public class NeuralNetwork {

    /**
     * list of matricies that hold weights of every link between two neurons
     */
    private Matrix[] layers;

    // some useful instances
    public static final NeuralNetwork IDENTITY = new NeuralNetwork(
            new Matrix[]{
                new Matrix(new double[][]{{0.0, 1.0}})
            }
    );

    /**
     * Creates a neural network with the given layer layout and random weights
     * between 0 and 1. Every neuron is conneted to every other.
     *
     * @param layers sequence of integers corresponding to layer sizes (e.g. 2 3
     * 2)
     */
    public NeuralNetwork(int... layers) {
        this.layers = new Matrix[layers.length];
        // size of previous layer
        int previous = 0;

        for (int i = 0; i < layers.length; i++) {
            if (previous != 0) {
                // add layer, +1 for bias
                this.layers[i] = Matrix.random(layers[i], previous + 1);
            }
            previous = layers[i];
        }
    }

    public NeuralNetwork(Matrix[] layers) {
        if (layers == null) {
            throw new IllegalArgumentException("List of layer cannot be null.");
        }
        if (layers.length < 1) {
            throw new IllegalArgumentException("There must be atleas one layer of neurons.");
        }
        for (int i = 1; i < layers.length; i++) {
            // -1 for bias
            if (layers[i].getColCount() - 1 != layers[i - 1].getRowCount()) {
                throw new IllegalArgumentException("Not valid layers, matrix dimensions do not corresponds.");
            }
        }
        this.layers = Arrays.copyOf(layers, layers.length);
    }

    public Matrix[] getLayers() {
        return Arrays.copyOf(this.layers, this.layers.length);
    }

    public void setLayers(Matrix[] layers) {
        for (int i = 1; i < layers.length; i++) {
            // -1 for bias
            if (layers[i].getColCount() - 1 != layers[i - 1].getRowCount()) {
                throw new IllegalArgumentException("Not valid layers, matrix dimensions do not corresponds.");
            }
        }

        this.layers = Arrays.copyOf(layers, layers.length);
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
            return layers[layer - 1].get(neuronNumber, weightNumber);
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
     * Computes ouput values for the given input values.
     *
     * @param inputs input values
     * @return output values of ouput neurons
     */
    public Matrix computeOutputs(Matrix inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        // -1 for bias
        if (inputs.getColCount() != 1 || inputs.getRowCount() != layers[0].getColCount() - 1) {
            throw new IllegalArgumentException("Input matrix does not have required dimensions. "
                    + "Got " + inputs.getRowCount() + "x" + inputs.getColCount()
                    + " but expected " + (layers[0].getColCount() - 1) + "x1.");
        }

        Matrix outputs = inputs;

        for (Matrix layer : layers) {
            // sum all inputs and apply activation function
            outputs = potentialsToOutputs(layer.multiply(addBias(outputs)));

        }

        return outputs;
    }

    /**
     * Coumputes output value in case there is only one output neuron.
     *
     * @param inputs vector of input values
     * @return output of the only output neuron
     */
    public double computeOutput(Matrix inputs) {
        if (layers[layers.length - 1].getRowCount() != 1) {
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
    private Matrix addBias(Matrix input) {
        double[][] newInput = new double[input.getRowCount() + 1][1];
        newInput[0][0] = 1;

        for (int i = 0; i < input.getRowCount(); i++) {
            newInput[i + 1][0] = input.getValues()[i][0];
        }

        return new Matrix(newInput);
    }

    /**
     * Applies activation funcion to potential of every neuron.
     *
     * @param potentials potentials of neurons
     * @return matrix with neurons' outputs
     */
    private Matrix potentialsToOutputs(Matrix potentials) {
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

        sb.append(layers[0].getColCount());

        for (Matrix layer : layers) {
            sb.append("-");
            sb.append(layer.getRowCount());
        }

        for (Matrix layer : layers) {
            sb.append("\n");
            sb.append(layer.toString());
        }

        return sb.toString();
    }
}
