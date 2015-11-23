package cz.muni.fi.walkauth;

import static java.lang.Math.abs;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Jaroslav Cechak
 */
public class NeuralNetworkNGTest {

    private static Matrix layer1;
    private static Matrix layer2;
    private static Matrix layer3;
    private static Matrix[] layers;
    private static Matrix[] layers3;
    private static Matrix testInput1;
    private static Matrix testInput2;
    private static Matrix expectedOutput1;
    private static Matrix expectedOutput2;
    private static double expectedOutput3;
    private static double epsilon;

    public NeuralNetworkNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        layer1 = new Matrix(
                new double[][]{
                    {1.0, 1.0, 1.0},
                    {0.0, 0.0, 0.0},
                    {0.5, 0.5, 0.5}
                }
        );

        layer2 = new Matrix(
                new double[][]{
                    {1.0, 2.0, 0.0, 0.0},
                    {1.0, 0.5, 0.0, 0.5}
                }
        );

        layer3 = new Matrix(
                new double[][]{
                    {1.0, 1.0, 1.0}

                }
        );

        layers = new Matrix[]{layer1, layer2};
        layers3 = new Matrix[]{layer1, layer2, layer3};

        testInput1 = new Matrix(
                new double[][]{
                    {1},
                    {0}
                }
        );

        testInput2 = new Matrix(
                new double[][]{
                    {1.5},
                    {1.5}
                }
        );

        expectedOutput1 = new Matrix(
                new double[][]{
                    {1.69910},
                    {1.55241}
                }
        );

        expectedOutput2 = new Matrix(
                new double[][]{
                    {1.70619},
                    {1.61148}
                }
        );

        expectedOutput3 = 1.7040927537987764;

        epsilon = 0.00001;
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of getLayers method, of class NeuralNetwork.
     */
    @Test
    public void testGetLayers() {
        NeuralNetwork instance = new NeuralNetwork(layers);
        Matrix[] result = instance.getLayers();

        assertEquals(result, layers, "Layer do not equal.");
    }

    /**
     * Test of getNeuronWeight method, of class NeuralNetwork.
     */
    @Test
    public void testGetNeuronWeight() {
        int layer = 2;
        int neuronNumber = 0;
        int weightNumber = 1;
        NeuralNetwork instance = new NeuralNetwork(layers);
        double expResult = 2.0;
        double result = instance.getNeuronWeight(layer, neuronNumber, weightNumber);
        assertEquals(result, expResult, "Did not recieved expected weight.");
    }

    /**
     * Test of computeOutputs method, of class NeuralNetwork.
     */
    @Test
    public void testComputeOutputs() {
        NeuralNetwork instance = new NeuralNetwork(layers);
        Matrix result = instance.computeOutputs(testInput1);
        assertTrue(result.getColCount() == 1, "Output is not a vector.");
        assertTrue(result.getRowCount() == 2, "Output vector has incorrect size.");

        for (int i = 0; i < result.getRowCount(); i++) {
            assertTrue(abs(result.get(i, 0) - expectedOutput1.get(i, 0)) < epsilon, "Forward computation did not ended with expected result.");
        }

        result = instance.computeOutputs(testInput2);
        for (int i = 0; i < result.getRowCount(); i++) {
            assertTrue(abs(result.get(i, 0) - expectedOutput2.get(i, 0)) < epsilon, "Forward computation did not ended with expected result.");
        }
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testSingleOutputFail() {
        NeuralNetwork n = new NeuralNetwork(layers);
        n.computeOutput(testInput1);
    }

    @Test
    public void testComputeOutput() {
        NeuralNetwork instance = new NeuralNetwork(layers3);
        double result = instance.computeOutput(testInput1);
        assertTrue(abs(result - expectedOutput3) < epsilon, "Forward computation did not ended with expected result.");
    }

}
