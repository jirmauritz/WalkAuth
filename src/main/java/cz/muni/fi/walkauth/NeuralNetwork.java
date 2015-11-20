package cz.muni.fi.walkauth;

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
    private final List<Matrix> layers = new LinkedList<>();

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
        if (layers.size() < 2) {
            throw new IllegalArgumentException("There must be atleas two layers of neurons.");
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
    public Matrix compute(Matrix inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        // -1 for bias
        if (inputs.getColCount() != 1 || inputs.getRowCount() != layers.get(0).getColCount() - 1) {
            throw new IllegalArgumentException("Input matrix does not have required dimensions. "
                    + "Got " + inputs.getRowCount() + "x" + inputs.getColCount()
                    + " but expected " + (layers.get(0).getColCount()-1) + "x1.");
        }

        Matrix output = inputs;

        for (Matrix layer : layers) {
            // sum all inputs and apply activation function
            output = computeOutputs(layer.multiply(addBias(output)));
            System.out.println("midresult:\n" + output);
        }

        return output;
    }

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
    private Matrix computeOutputs(Matrix potentials) {
        if (potentials == null) {
            throw new IllegalArgumentException("Potetial matrix cannot be null.");
        }
        System.out.println("potetials:\n" + potentials);
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
