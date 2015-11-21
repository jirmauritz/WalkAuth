package cz.muni.fi.walkauth.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Purpose of this class is to provide prepared data for learning.
 *
 * @author xmauritz
 */
public class DataManager {

	// normalization
	private Normalization normalization = new Normalization();
	
	// data
	private List<Sample> trainingData;
	private List<Sample> testingData;
	private List<Sample> validationData;
	
	public void prepareData(
			int entriesForSample,
			String dataPath,
			String positiveUserFilePath,
			float trainDataRatio,
			float testDataRatio,
			float verifyDataRatio
	) throws IOException {
		
		// crear data
		trainingData = new ArrayList<>();
		testingData = new ArrayList<>();
		validationData = new ArrayList<>();

		// verify ratios
		if (Math.abs(trainDataRatio + testDataRatio + verifyDataRatio - 1) > 0.0001) {
			System.out.println(trainDataRatio + testDataRatio + verifyDataRatio - 1);
			throw new IllegalArgumentException("Ratios of train " + trainDataRatio
					+ ", test " + testDataRatio + " and verify data " + verifyDataRatio + " has to be 1 in sum.");
		}

		// load positive user data
		List<Sample> positiveUserData = getSamplesFromFile(new File(dataPath + positiveUserFilePath), entriesForSample, true);

		// load other data
		List<Sample> negativeUserData = new ArrayList<>();
		File sampleDir = new File(dataPath);
		for (File walkDataFile : sampleDir.listFiles()) {
			// skip the positive user data file
			if (walkDataFile.getName().equals(positiveUserFilePath)) {
				break;
			}
			// load walk data
			negativeUserData.addAll(getSamplesFromFile(walkDataFile, entriesForSample, false));
		}

		// number of data samples
		int positiveNum = positiveUserData.size();
		int negativeNum = negativeUserData.size();

		// create training data
		int trainPosSamplesNum = Math.round(positiveNum * trainDataRatio);
		int trainNegSamplesNum = Math.round(negativeNum * trainDataRatio);
		generateData(trainingData, positiveUserData, negativeUserData, trainPosSamplesNum, trainNegSamplesNum);

		// create train data
		int testUserSamplesNum = Math.round(positiveNum * testDataRatio);
		int testOthersSamplesNum = Math.round(negativeNum * testDataRatio);
		generateData(testingData, positiveUserData, negativeUserData, testUserSamplesNum, testOthersSamplesNum);

		// create verify data
		generateData(validationData, positiveUserData, negativeUserData, positiveUserData.size(), negativeUserData.size());
		
		// normalization		
		List<Sample> allInOne = new ArrayList<>(trainingData);
		allInOne.addAll(testingData);
		allInOne.addAll(validationData);
		// compute mean and deviation from all
		normalization.computeMeanAndDeviation(allInOne);
		// normalize each
		trainingData = normalization.normalize(trainingData);
		testingData = normalization.normalize(testingData);
		validationData = normalization.normalize(validationData);
	}

	public String dataOverview() {
		StringBuilder sb = new StringBuilder();

		sb.append("Each sample has ");
		sb.append(trainingData.get(0).getEntries().length);
		sb.append(" values.\n");
		sb.append("train data size: ");
		sb.append(trainingData.size());
		sb.append(" samples\n");
		sb.append("test data size: ");
		sb.append(testingData.size());
		sb.append(" samples\n");
		sb.append("verify data size: ");
		sb.append(validationData.size());
		sb.append(" samples\n\n");
		sb.append("Original mean value: ");
		sb.append(normalization.getMean());
		sb.append(", original standart deviation: ");
		sb.append(normalization.getDeviation());
		sb.append("\n\n");				
		sb.append("Data density (| positive user, - negative user)\n");
		sb.append("training data density: [");
		for (Sample s : trainingData) {
			if (s.isPositiveUserData()) {
				sb.append("|");
			} else {
				sb.append("-");
			}
		}
		sb.append("]\n");

		sb.append("testing data density: [");
		for (Sample s : testingData) {
			if (s.isPositiveUserData()) {
				sb.append("|");
			} else {
				sb.append("-");
			}
		}
		sb.append("]\n");
		sb.append("valifation data density: [");
		for (Sample s : validationData) {
			if (s.isPositiveUserData()) {
				sb.append("|");
			} else {
				sb.append("-");
			}
		}
		sb.append("]\n\n");
		sb.append("Example of first positive testing sample: [");
		for (int i=0; i<5; i++) {
			sb.append(testingData.get(0).getEntries()[i]);
			sb.append(", ");
		}
		sb.append(testingData.get(0).getEntries()[5]);
		sb.append(", ...");

		return sb.toString();
	}

	/**
	 * The returned list of samples contains samples created of entries of that file.
	 * WARNING: the Samples are always indented to the entriesForSample count, i.e. 
	 * last (entriesInFile % entriesForSample) entries are thrown away
	 */
	private List<Sample> getSamplesFromFile(File file, int entriesForSample, boolean isPositiveUserData) throws IOException {
		BufferedReader reader;

		// prepare raw file dor reading
		reader = new BufferedReader(new FileReader(file));

		// initialize set of samples
		List<Sample> listOfSamples = new ArrayList<>();
		double[] entries = new double[3*entriesForSample];
		int entryCount = 0;

		String line = reader.readLine();
		// for every line of file
		while (line != null) {
			if (entryCount < 3*entriesForSample) {
				String[] coordinates = line.split(",");
				// check if there are 3 coordinates
				if (coordinates.length != 4) {
					throw new IllegalStateException("The entry " + line + " has more coordinates than 3");
				}
				// the first coordinate (0) is time - we do not need it
				entries[entryCount++] = Double.parseDouble(coordinates[1]);
				entries[entryCount++] = Double.parseDouble(coordinates[2]);
				entries[entryCount++] = Double.parseDouble(coordinates[3]);
			} else {
				// new sample
				Sample sample = new Sample(isPositiveUserData, entries);
				listOfSamples.add(sample);
				// reset counter
				entryCount = 0;
			}
			line = reader.readLine();
		}
		return listOfSamples;
	}

	private void generateData(List<Sample> data, List<Sample> userData, List<Sample> othersData, int userNum, int othersNum) {
		Random rnd = new Random(System.currentTimeMillis());

		// compute ratio
		double ratio = userNum * 1.0 / othersNum;
		ratio *= 100;

		while (userNum > 0 || othersNum > 0) {
			// if there is no sample, break
			if (userData.isEmpty() && othersData.isEmpty()) {
				break;
			}
			// decide whether to add user or other data
			if ((userNum == 0) || (userData.isEmpty())) {
				data.add(othersData.remove(rnd.nextInt(othersData.size())));
				othersNum--;
			} else if ((othersNum == 0) || (othersData.isEmpty())) {
				data.add(userData.remove(rnd.nextInt(userData.size())));
				userNum--;
			} else {
				if (rnd.nextInt(100) > ratio) {
					data.add(othersData.remove(rnd.nextInt(othersData.size())));
					othersNum--;
				} else {
					data.add(userData.remove(rnd.nextInt(userData.size())));
					userNum--;
				}
			}
		}
	}

	public List<Sample> getTrainData() {
		return trainingData;
	}

	public void setTrainData(List<Sample> trainData) {
		this.trainingData = trainData;
	}

	public List<Sample> getTestData() {
		return testingData;
	}

	public void setTestData(List<Sample> testData) {
		this.testingData = testData;
	}

	public List<Sample> getVerifyData() {
		return validationData;
	}

	public void setVerifyData(List<Sample> verifyData) {
		this.validationData = verifyData;
	}

	public Normalization getNormalization() {
		return normalization;
	}	
}
