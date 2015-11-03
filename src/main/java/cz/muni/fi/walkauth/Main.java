package cz.muni.fi.walkauth;

/**
 * 
 * 
 * @author Jiri Mauritz : jirmauritz at gmail dot com
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		double[][] values = {{1.2, 2.3}, {0.0, 0.12345}};
		Matrix m = new Matrix(values);
		System.out.println(m);
		
	}
	
}
