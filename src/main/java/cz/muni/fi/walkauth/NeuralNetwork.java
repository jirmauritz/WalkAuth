package cz.muni.fi.walkauth;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private List<Matrix> layers = new LinkedList<>();

    // some useful instances
    public static final NeuralNetwork IDENTITY = new NeuralNetwork(
            Arrays.asList(
                    new Matrix(new double[][]{{0.0, 1.0}})
            )
    );

    /**
     * Creates a neural network with the given layer layout and random weights
     * between 0 and 1. Every neuron is conneted to every other.
     *
     * @param layers sequence of integers corresponding to layer sizes (e.g. 2 3
     * 2)
     */
    public NeuralNetwork(int... layers) {
        // size of previous layer
        int previous = 0;

        for (int layer : layers) {
            if (previous != 0) {
                // add layer, +1 for bias
                this.layers.add(Matrix.random(layer, previous + 1));
            }
            previous = layer;
        }
    }

    public NeuralNetwork(List<Matrix> layers) {
        if (layers == null) {
            throw new IllegalArgumentException("List of layer cannot be null.");
        }
        if (layers.size() < 1) {
            throw new IllegalArgumentException("There must be atleas one layer of neurons.");
        }
        for (int i = 1; i < layers.size(); i++) {
            // -1 for bias
            if (layers.get(i).getColCount() - 1 != layers.get(i - 1).getRowCount()) {
                throw new IllegalArgumentException("Not valid layers, matrix dimensions do not corresponds.");
            }
        }
        this.layers.addAll(layers);
    }

    public List<Matrix> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    public void setLayers(List<Matrix> layers) {
        this.layers = layers;
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
            return layers.get(layer - 1).get(neuronNumber, weightNumber);
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
        if (inputs.getColCount() != 1 || inputs.getRowCount() != layers.get(0).getColCount() - 1) {
            throw new IllegalArgumentException("Input matrix does not have required dimensions. "
                    + "Got " + inputs.getRowCount() + "x" + inputs.getColCount()
                    + " but expected " + (layers.get(0).getColCount() - 1) + "x1.");
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
        if (layers.get(layers.size() - 1).getRowCount() != 1) {
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

        sb.append(layers.get(0).getColCount());

        for (int i = 0; i < layers.size(); i++) {
            sb.append("-");
            sb.append(layers.get(i).getRowCount());
        }

        for (Matrix layer : layers) {
            sb.append("\n");
            sb.append(layer.toString());
        }

        return sb.toString();
    }
}
