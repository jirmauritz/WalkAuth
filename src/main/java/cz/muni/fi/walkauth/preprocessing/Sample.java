package cz.muni.fi.walkauth.preprocessing;

import java.util.Arrays;

/**
 * Representation of a sample as a set of entries from walk data.
 * 
 * @author xmauritz
 */
public final class Sample {
	
	// every entry has 3D array with x, y and z coordinates
	private final double[] entries;
	
	// true if the sample belongs to the testing user
	private final boolean positiveUserData;
	
	public Sample(boolean isPositiveUserData, double[] entries) {
		this.positiveUserData = isPositiveUserData;
		this.entries = entries;
	}
	
	public double[] getEntries() {
		return entries;
	}

	public boolean isPositiveUserData() {
		return positiveUserData;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Sample{ entries:\n");
		sb.append(Arrays.toString(entries));
		return sb.toString();
	}
}
