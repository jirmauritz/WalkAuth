package cz.muni.fi.walkauth;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for Matrix class.
 */
public class MatrixTest {
	
	private Matrix m1;
	
	public MatrixTest() {
	}
	
	@Before
	public void setUp() {
		double[][] values1 = {{1.1, 1.2}, {2.3, 3.4}, {-1.5, -0.2}};
		m1 = new Matrix(values1);
	}

	/**
	 * Test of getRowCount method, of class Matrix.
	 */
	@Test
	public void testGetRowCount() {
		int result = m1.getRowCount();
		assertEquals(3, result);
	}

	/**
	 * Test of getColCount method, of class Matrix.
	 */
	@Test
	public void testGetColCount() {
		int result = m1.getColCount();
		assertEquals(2, result);
	}

	/**
	 * Test of get method, of class Matrix.
	 */
	@Test
	public void testGet() {
		double result = m1.get(2, 1);
		assertEquals(-0.2, result, 0.000001);
	}
        
        /**
	 * Test of multiply method, of class Matrix.
	 */
        @Test
        public void testMultiply() {
            double epsilon = 0.0000000001;
            
            double[][] values2 = {{1.1, 1.2, 1.3}, {2.1, 2.2, 2.3}};
            Matrix m2 = new Matrix(values2);
            
            double[][] expectedValues = {{3.73, 3.96, 4.19}, {9.67, 10.24, 10.81},{-2.07, -2.24, -2.41}};
            Matrix expected = new Matrix(expectedValues);
            
            Matrix product = m1.multiply(m2);
            
            assertEquals("Product have unexpected number of rows.", 3, product.getRowCount());
            assertEquals("Product have unexpected number of columns.", 3, product.getColCount());
            
            for(int i = 0; i < product.getRowCount(); i++) {
                for (int j = 0; j < product.getColCount(); j++) {
                    assertTrue("Unexpected value on row " + i + " column " + j + ".",
                            Math.abs(product.get(i, j) - expected.get(i,j)) < epsilon);
                }
            }
        }
        
        /**
	 * Test of multiply method, of class Matrix.
	 */
        @Test(expected = IllegalArgumentException.class)
        public void testMultiplyWrongDimensions() {
            m1.multiply(m1);
        }


	
}
