package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sample;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for backpropagation algorithm.
 */
public class BackpropagationTest {

    private static final double EPSILON = 0.001;

    private static void assertMatriciesAlmostEqual(Matrix m1, Matrix m2) {
        assertEquals(m1.getRowCount(), m2.getRowCount());
        assertEquals(m1.getColCount(), m2.getColCount());

        for (int i = 0; i < m1.getRowCount(); i++) {
            for (int j = 0; j < m1.getColCount(); j++) {
                assertEquals(m1.get(i, j), m2.get(i, j), EPSILON,
                    "Matricies " + m1 + " and " + m2 + " differ at position " + i + ", " + j);
            }
        }
    }

    private static void assertMatrixListsAlmostEqual(Matrix[] ml1, Matrix[] ml2) {
        assertEquals(ml1.length, ml2.length);
        for (int i = 0; i < ml1.length; i++) {
            assertMatriciesAlmostEqual(ml1[i], ml2[i]);
        }
    }

    /**
     * This function computes gradient numerically. Partial derivative of error
     * with respect each weight is computed as a change in error over given data
     * when we change the weight a little bit (divided by this "little bit").
     * This is inefficient and should only be used for testing that
     * backpropagation works.
     */
    private static Matrix[] computeGradientNumerically(NeuralNetwork neuralNetwork, Sample[] data) {
        final double DELTA = 0.00001;
        Matrix[] weights = neuralNetwork.getWeights();
        int layersCount = weights.length;

        // create gradient matricies list of correct size
        Matrix[] gradient = new Matrix[layersCount];
        for (int l = 0; l < layersCount; l++) {
            gradient[l] = Matrix.zeros(weights[l].getRowCount(), weights[l].getColCount());
        }

        // for each weight, compute its partial derivative numerically
        for (int l = 0; l < layersCount; l++) {
            for (int i = 0; i < weights[l].getRowCount(); i++) {
                for (int j = 0; j < weights[l].getColCount(); j++) {
                    double w = weights[l].get(i, j);

                    double wLeft = w - DELTA;
                    Matrix[] weightsLeft = neuralNetwork.getWeights();
                    weightsLeft[l].set(i, j, wLeft);
                    NeuralNetwork neuralNetworkLeft = new NeuralNetwork(weightsLeft);
                    double errorLeft = Evaluation.computeError(neuralNetworkLeft, data);

                    double wRight = w + DELTA;
                    Matrix[] weightsRight = neuralNetwork.getWeights();
                    weightsRight[l].set(i, j, wRight);
                    NeuralNetwork neuralNetworkRight = new NeuralNetwork(weightsRight);
                    double errorRight = Evaluation.computeError(neuralNetworkRight, data);

                    double derivative = (errorRight - errorLeft) / (wRight - wLeft);
                    gradient[l].set(i, j, derivative);
                }
            }
        }

        return gradient;
    }

    @BeforeMethod
    public void setUp() {
    }

    /**
     * Test of backpropagation for a no sample.
     */
    @Test
    public void test_no_sample() {
        // given
        NeuralNetwork ANY_NETWORK = new NeuralNetwork(1, 1);
        Sample[] data = {};

        // action
        Matrix[] gradient = NeuralNetworkLearning.backpropagation(ANY_NETWORK, data);

        // assert
        Matrix[] expectedGradient = {Matrix.zeros(1, 2)};
        assertMatrixListsAlmostEqual(gradient, expectedGradient);
    }

    /**
     * Test of backpropagation for a simple situation with zero gradient.
     */
    @Test
    public void test_zero_gradient_for_positive_positive() {
        // given
        NeuralNetwork neuralNetwork = new NeuralNetwork(new Matrix[]{
            new Matrix(new double[][]{{1.0, 0.0}})
        });
        double[] ANY_INPUT = new double[]{0};
        Sample[] data = {new Sample(true, ANY_INPUT)};

        // action
        Matrix[] gradient = NeuralNetworkLearning.backpropagation(neuralNetwork, data);

        // assert
        Matrix[] expectedGradient = {Matrix.zeros(1, 2)};
        assertMatrixListsAlmostEqual(gradient, expectedGradient);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckZeroGradient() {
        Matrix[] weights = {
            new Matrix(new double[][]{{1.0, 0.0}})
        };
        Sample[] data = {
            new Sample(true, new double[]{0})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckZeroWeights() {
        Matrix[] weights = {
            new Matrix(new double[][]{{0.0, 0.0}})
        };
        Sample[] data = {
            new Sample(true, new double[]{0})
        };
        // gradient guess: 0th weight negative, 1st zero
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckPositiveWeights() {
        Matrix[] weights = {
            new Matrix(new double[][]{{1.0, 1.0}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1})
        };
        // gradient guess: both weights positive (same)
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckNegativeWeights() {
        Matrix[] weights = {
            new Matrix(new double[][]{{-1.0, -1.0}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckTwoSamples() {
        Matrix[] weights = {
            new Matrix(new double[][]{{1.0, 1.0}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1}),
            new Sample(true, new double[]{1})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckLongerInput() {
        Matrix[] weights = {
            new Matrix(new double[][]{{0.5, 0.7, -0.1, 0.8, 0.4}})
        };
        Sample[] data = {
            new Sample(false, new double[]{0.1, 0.0, -0.5, 1.2}),
            new Sample(true, new double[]{1.1, -0.2, -0.4, 0.0})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckTwoSimpleLayers() {
        Matrix[] weights = {
            new Matrix(new double[][]{{0.5, 0.5}}),
            new Matrix(new double[][]{{0.0, 1.0}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1.0})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckTwoLayers() {
        Matrix[] weights = {
            new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}}),
            new Matrix(new double[][]{{0.0, 1.0, 2.0}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1.0})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckTwoLayers2() {
        Matrix[] weights = {
            new Matrix(new double[][]{{0.5, 0.4, 0.3}, {-0.1, 0.2, 0.6}}),
            new Matrix(new double[][]{{0.7, -0.8, 1.2}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1.0, 0.7})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckThreeLayers() {
        Matrix[] weights = {
            new Matrix(new double[][]{{0.5, 0.4, 0.3}, {-0.1, 0.2, 0.6}}),
            new Matrix(new double[][]{{0.7, -0.8, 1.2}, {0.1, 0.5, -1.2}}),
            new Matrix(new double[][]{{0.1, -0.4, 1.0}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1.0, 0.7})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     */
    @Test
    public void testBackpropagationNumericCheckComplexNetwork() {
        Matrix[] weights = {
            new Matrix(new double[][]{{0.5, 0.4, 0.3, 0.6}, {-0.1, 0.2, 0.6, 0.1}}),
            new Matrix(new double[][]{{0.7, -0.8, 1.2}, {0.1, 0.5, -1.2}, {-0.2, -0.4, 0.5}}),
            new Matrix(new double[][]{{0.1, -0.4, 1.0, 0.8}, {0.2, 0.4, 0.8, -0.5}}),
            new Matrix(new double[][]{{0.5, 0.7, -0.5}})
        };
        Sample[] data = {
            new Sample(false, new double[]{1.0, 0.7, 0.1}),
            new Sample(true, new double[]{-1.0, 0.6, -0.6}),
            new Sample(true, new double[]{0.0, 0.1, 1.1})
        };
        gradientNumericCheck(weights, data);
    }

    /**
     * Compare backpropagation result to gradient computed numerically.
     *
     * @param weights configuration of a neural network
     * @param data data on which to calculate the error
     */
    private void gradientNumericCheck(Matrix[] weights, Sample[] data) {
        // given
        NeuralNetwork neuralNetwork = new NeuralNetwork(weights);

        // action
        Matrix[] backpropGradient = NeuralNetworkLearning.backpropagation(neuralNetwork, data);

        // assert
        Matrix[] expectedGradient = computeGradientNumerically(neuralNetwork, data);
        assertMatrixListsAlmostEqual(backpropGradient, expectedGradient);
    }

}
