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
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Arrays.hashCode(this.entries);
		hash = 59 * hash + (this.positiveUserData ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Sample other = (Sample) obj;
		
		// entries
		if (other.entries.length != this.entries.length) {
			return false;
		}
		for (int i = 0; i < other.entries.length; i++) {
			if (!almostEquals(other.entries[i], this.entries[i])) {
				return false;
			}
		}
		
		if (this.positiveUserData != other.positiveUserData) {
			return false;
		}
		return true;
	}
	
	private boolean almostEquals(double a, double b) {
		return Math.abs(a-b) < 0.000001;
	}

	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Sample{ positive: " + positiveUserData + " \n entries:\n");
		sb.append(Arrays.toString(entries));
		return sb.toString();
	}
}
