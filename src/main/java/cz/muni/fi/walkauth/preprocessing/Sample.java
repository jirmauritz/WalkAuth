package cz.muni.fi.walkauth.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a sample as a set of entries from walk data.
 * 
 * @author xmauritz
 */
public class Sample {
	
	// every entry has 3D array with x, y and z coordinates
	private final List<double[]> entries;
	
	// true if the sample belongs to the testing user
	private boolean testingUserData;
	
	public Sample() {
		entries = new ArrayList<>();
	}
	
	public List<double[]> getEntries() {
		return entries;
	}
	
	public void add(double[] entry) {
		if (entry.length != 3) {
			throw new IllegalArgumentException("Length of an entry has to be 3.");
		}
		entries.add(entry);
	}

	public boolean isTestingUserData() {
		return testingUserData;
	}

	public void setTestingUserData(boolean testingUserData) {
		this.testingUserData = testingUserData;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Sample{ entries:\n");
		for (double[] entry : entries) {
			sb.append("x: ");
			sb.append(entry[0]);
			sb.append(", y: ");
			sb.append(entry[1]);
			sb.append(", z: ");
			sb.append(entry[2]);
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
