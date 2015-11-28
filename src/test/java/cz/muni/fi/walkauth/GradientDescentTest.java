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

    private Sample[] samples1;

    private Sample[] validationSamples1;
    
    private Sample[] samples2;
    
    private Sample[] validationSamples2;

    @BeforeMethod
    public void beforeMethod() {
        Matrix[] weightsId = new Matrix[1];
        weightsId[0] = new Matrix(new double[][]{{0, 1}});

        id = new NeuralNetwork(weightsId);

        Sample s1 = new Sample(true, new double[]{1});
        samples1 = new Sample[]{s1};
        validationSamples1 = new Sample[]{s1};
        
        Sample s2 = new Sample(true, new double[] {0});
        samples2 = new Sample[] {s2};
        validationSamples2 = new Sample[]{s2};
    }

    @Test
    public void testId() {
        NeuralNetwork trained = NeuralNetworkLearning.gradienDescent(id, samples1, validationSamples1, 0.1, (Integer) -> 0.1);
        assertEquals(trained.getWeights(), id.getWeights(), "No training should have been made to network.");
    }

    @Test
    public void testLearing() {
        NeuralNetwork trained = NeuralNetworkLearning.gradienDescent(id, samples2, validationSamples2, 0.1, (Integer) -> 0.1);
        assertNotEquals(trained.getWeights(), id.getWeights(), "Neural network should have learned something.");
    }
}
