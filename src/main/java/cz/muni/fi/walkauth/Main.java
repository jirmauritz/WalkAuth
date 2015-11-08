package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.Sampler;

/**
 * 
 * 
 * @author Jiri Mauritz : jirmauritz at gmail dot com
 */
public class Main {

	/**
	 * @param args the command line arguments
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception {
		Sampler s = new Sampler();
		s.sample(100);
	}
}
