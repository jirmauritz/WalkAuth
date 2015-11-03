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


	
}
