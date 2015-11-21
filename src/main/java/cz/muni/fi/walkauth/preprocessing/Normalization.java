package cz.muni.fi.walkauth.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Class provides tools for data normalization.
 *
 *
 * @author Jiri Mauritz: jirmauritz at gmail dot com
 */
public class Normalization {

	private Double mean = null;
	private Double deviation = null;
	
	/**
	 * Computes mean and deviation of the given set of data.
	 * The method does NOT change the data.
	 * 
	 * @param samples - data
	 */
	public void computeMeanAndDeviation(List<Sample> samples) {
		computeMeanValues(samples);
		computeDeviation(samples);
	}
	
	/**
	 * Provides data normalization - mean will be 0 and deviation 1.
	 * It is necessary to run computeMeanAndDeviation first.
	 *
	 * @param samples - data to normalize
	 * @return list of normalized samples
	 */
	public List<Sample> normalize(List<Sample> samples) {
		int entriesForSample = samples.get(0).getEntries().length;
		List<Sample> normalized = new ArrayList<>();
		for (Sample s : samples) {
			double[] normalizedEntries = new double[entriesForSample];
			int entryCounter = 0;
			for (double entry : s.getEntries()) {
				normalizedEntries[entryCounter++] = normalizeValue(entry);
			}
			normalized.add(new Sample(s.isPositiveUserData(), normalizedEntries));
		}
		
		return normalized;
	}
	
	private double normalizeValue(double value) {
		return (value - mean) / deviation;
	}

	private void computeMeanValues(List<Sample> samples) {
		double sum = 0;
		for (Sample s : samples) {
			for (double entry : s.getEntries()) {
				sum += entry;
			}
		}
		mean = sum / (samples.size() * samples.get(0).getEntries().length);
	}
	
	private void computeDeviation(List<Sample> samples) {
		if (mean == null) {
			computeMeanValues(samples);
		}
		double sum = 0;
		for (Sample s : samples) {
			for (double entry : s.getEntries()) {
				sum += Math.pow((entry - mean), 2);
			}
		}
		deviation = Math.sqrt(sum / (samples.size() * samples.get(0).getEntries().length));
	}

	public Double getMean() {
		return mean;
	}

	public Double getDeviation() {
		return deviation;
	}

}
