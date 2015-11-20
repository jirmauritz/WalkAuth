package cz.muni.fi.walkauth;

import static java.lang.Math.abs;
import java.util.LinkedList;
import java.util.List;
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

    private static double[][] layer1;
    private static double[][] layer2;
    private static List<Matrix> layers;
    private static double[][] testInput1;
    private static double[][] testInput2;
    private static double[][] expectedOutput1;
    private static double[][] expectedOutput2;
    private static double epsilon;

    public NeuralNetworkNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        layer1 = new double[][]{
            {1.0, 1.0, 1.0},
            {0.0, 0.0, 0.0},
            {0.5, 0.5, 0.5},};
        layer2 = new double[][]{
            {1.0, 2.0, 0.0, 0.0},
            {1.0, 0.5, 0.0, 0.5}
        };

        layers = new LinkedList<>();
        layers.add(new Matrix(layer1));
        layers.add(new Matrix(layer2));

        testInput1 = new double[][]{
            {1},
            {0}
        };
        
        testInput2 = new double[][] {
            {1.5},
            {1.5}
        };

        expectedOutput1 = new double[][]{
            {1.69910},
            {1.55241}
        };
        
        expectedOutput2 = new double[][] {
            {1.70619},
            {1.61148}
        };

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
        List<Matrix> result = instance.getLayers();

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
     * Test of compute method, of class NeuralNetwork.
     */
    @Test
    public void testCompute() {
        NeuralNetwork instance = new NeuralNetwork(layers);
        Matrix result = instance.compute(new Matrix(testInput1));
        assertTrue(result.getColCount() == 1, "Output is not a vector.");
        assertTrue(result.getRowCount() == 2, "Output vector has incorrect size.");
        
        for (int i = 0; i < result.getRowCount(); i++) {
            assertTrue(abs(result.get(i, 0) - expectedOutput1[i][0]) < epsilon, "Forward computation did not ended with expected result.");
        }
        
        result = instance.compute(new Matrix(testInput2));
        for (int i = 0; i < result.getRowCount(); i++) {
            assertTrue(abs(result.get(i, 0) - expectedOutput2[i][0]) < epsilon, "Forward computation did not ended with expected result.");
        }
    }

}
