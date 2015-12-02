package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test is to verify that the gradient descent algorithm is correctly
 * implemneted.
 *
 * @author Jaroslav Cechak
 */
public class GradientDescentTest {

    private NeuralNetwork id;

    private Sample[] samples;

    private Sample[] validationSamples;

    private NeuralNetwork n;

    @BeforeMethod
    public void beforeMethod() {
        Matrix[] weightsId = new Matrix[1];
        weightsId[0] = new Matrix(new double[][]{{0, 1}});

        id = new NeuralNetwork(weightsId);

    }

    @Test
    public void testId() {
        Sample s = new Sample(true, new double[]{1});
        samples = new Sample[]{s};
        validationSamples = new Sample[]{s};

        NeuralNetwork trained = NeuralNetworkLearning.gradienDescent(id, samples, validationSamples, 0.1, (Integer) -> 0.1, 100);
        assertEquals(trained.getWeights(), id.getWeights(), "No training should have been made to network.");
    }

    @Test
    public void testLearing() {
        Sample s = new Sample(true, new double[]{0});
        samples = new Sample[]{s};
        validationSamples = new Sample[]{s};

        NeuralNetwork trained = NeuralNetworkLearning.gradienDescent(id, samples, validationSamples, 0.1, (Integer) -> 0.1, 100);
        assertNotEquals(trained.getWeights(), id.getWeights(), "Neural network should have learned something.");
        // test bias
        assertTrue(trained.getNeuronWeight(1, 0, 0) > 0, "Bias should have risen.");
    }

    @Test
    public void testLearningMultipleInputNeurons() {
        Sample s = new Sample(false, new double[]{1, 1});
        samples = new Sample[]{s};
        validationSamples = new Sample[]{s};

        Matrix[] weightsId = new Matrix[1];
        weightsId[0] = new Matrix(new double[][]{{1, 1, 1}});

        n = new NeuralNetwork(weightsId);

        NeuralNetwork trained = NeuralNetworkLearning.gradienDescent(n, samples, validationSamples, 0.1, (Integer) -> 0.1, 100);
        assertNotEquals(trained.getWeights(), id.getWeights(), "Neural network should have learned something.");
        // test bias
        assertTrue(trained.getNeuronWeight(1, 0, 0) < 0, "Bias should have dropped.");
        // test 1st input
        assertTrue(trained.getNeuronWeight(1, 0, 1) < 0, "Weight should have dropped.");
        // test 2nd input
        assertTrue(trained.getNeuronWeight(1, 0, 2) < 0, "Weight should have dropped.");
    }

    @Test
    public void testLearningMultipleLayers() {
        Sample s = new Sample(true, new double[]{1});
        samples = new Sample[]{s};
        validationSamples = new Sample[]{s};

        Matrix[] weightsId = new Matrix[2];
        weightsId[0] = new Matrix(new double[][]{{1, 1}});
        weightsId[1] = new Matrix(new double[][]{{1, 1}});

        n = new NeuralNetwork(weightsId);

        NeuralNetwork trained = NeuralNetworkLearning.gradienDescent(n, samples, validationSamples, 0.1, (Integer) -> 0.1, 100);
        assertNotEquals(trained.getWeights(), id.getWeights(), "Neural network should have learned something.");
        // test bias hidden neuron
        assertTrue(trained.getNeuronWeight(1, 0, 0) < 1, "Bias should have dropped.");
        // test input
        assertTrue(trained.getNeuronWeight(1, 0, 1) < 1, "Weight should have dropped.");
        // test bias of output neuron
        assertTrue(trained.getNeuronWeight(2, 0, 0) < 1, "Bias should have dropped.");
        // test hidden neuron -> output neuron
        assertTrue(trained.getNeuronWeight(2, 0, 1) < 1, "Weight should have dropped.");
    }
}
