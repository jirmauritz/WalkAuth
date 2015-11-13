/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.walkauth;

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
                // add layer
                this.layers.add(Matrix.random(layer, previous));
            }
            previous = layer;
        }
    }

    /**
     * Gets the weight of given neuron.
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
     * @param layer layer in which neuron is
     * @param neuronNumber position in layer
     * @param weightNumber position in neuron
     * @param weight weight to insert
     */
    public void setNeuronWeight(int layer, int neuronNumber, int weightNumber, double weight) {
        if (layer == 0) {
            throw new IllegalArgumentException("Input neurons do not have weights.");
        } else {
            layers.get(layer - 1).set(neuronNumber, weightNumber, weight);
        }
    }

    /**
     * Computes ouput values for the given input values.
     *
     * @param inputs input values
     * @return output values of ouput neurons
     */
    public Matrix compute(Matrix inputs) {
        Matrix output = inputs;

        for (Matrix layer : layers) {
            // sum all inputs and apply activation function
            output = computeOutputs(layer.multiply(output));
        }

        return output;
    }

    /**
     * Applies activation funcion to potential of every neuron.
     *
     * @param potentials potentials of neurons
     * @return matrix with neurons' outputs
     */
    private Matrix computeOutputs(Matrix potentials) {
        for (int i = 0; i < potentials.getColCount(); i++) {
            double potential = potentials.get(0, i);
            potentials.set(0, i, ActivationUtils.activationFunction(potential));
        }

        return potentials;
    }

    public void learn() {
        throw new UnsupportedOperationException("Not implemented yet.");
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
            sb.append("\n" + layer.toString());
        }

        return sb.toString();
    }
}
