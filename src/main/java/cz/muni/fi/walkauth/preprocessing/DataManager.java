package cz.muni.fi.walkauth.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Purpose of this class is to provide prepared data for learning.
 *
 * @author xmauritz
 */
public class DataManager {

	private final Sampler sampler = new Sampler();

	// data
	private List<Sample> trainData = new ArrayList<>();
	private List<Sample> testData = new ArrayList<>();
	private List<Sample> verifyData = new ArrayList<>();

	public void prepareData(
			int entriesForSample,
			String dataPath,
			String testingUserFilePath,
			float trainDataRatio,
			float testDataRatio,
			float verifyDataRatio
	) throws IOException {

		// verify ratios
		if (Math.abs(trainDataRatio + testDataRatio + verifyDataRatio - 1) > 0.0001) {
			System.out.println(trainDataRatio + testDataRatio + verifyDataRatio - 1);
			throw new IllegalArgumentException("Ratios of train " + trainDataRatio
					+ ", test " + testDataRatio + " and verify data " + verifyDataRatio + " has to be 1 in sum.");
		}

		// sample
		if (!(sampler.isSampled())) {
			sampler.sample(entriesForSample);
		}

		// load testing user data
		List<Sample> testingUserData = getSamplesFromFile(new File(dataPath + testingUserFilePath), true);

		// load other data
		List<Sample> othersData = new ArrayList<>();
		File sampleDir = new File(dataPath);
		for (File walkDataFile : sampleDir.listFiles()) {
			// skip the user testing data file
			if (walkDataFile.getName().equals(testingUserFilePath)) {
				break;
			}
			// load walk data
			othersData.addAll(getSamplesFromFile(walkDataFile, false));
		}

		// number of data samples
		int userNum = testingUserData.size();
		int othersNum = othersData.size();

		// create train data
		int trainUserSamplesNum = Math.round(userNum * trainDataRatio);
		int trainOthersSamplesNum = Math.round(othersNum * trainDataRatio);
		generateData(trainData, testingUserData, othersData, trainUserSamplesNum, trainOthersSamplesNum);

		// create train data
		int testUserSamplesNum = Math.round(userNum * testDataRatio);
		int testOthersSamplesNum = Math.round(othersNum * testDataRatio);
		generateData(testData, testingUserData, othersData, testUserSamplesNum, testOthersSamplesNum);

		// create verify data
		generateData(verifyData, testingUserData, othersData, testingUserData.size(), othersData.size());

	}

	public String dataOverview() {
		StringBuilder sb = new StringBuilder();

		sb.append("train data size: ");
		sb.append(trainData.size());
		sb.append("\n");
		sb.append("test data size: ");
		sb.append(testData.size());
		sb.append("\n");
		sb.append("verify data size: ");
		sb.append(verifyData.size());
		sb.append("\n");
		sb.append("\n");
		sb.append("train data density: [");
		for (Sample s : trainData) {
			if (s.isTestingUserData()) {
				sb.append("|");
			} else {
				sb.append("-");
			}
		}
		sb.append("]");
		sb.append("\n");
		sb.append("\n");

		sb.append("test data density: [");
		for (Sample s : testData) {
			if (s.isTestingUserData()) {
				sb.append("|");
			} else {
				sb.append("-");
			}
		}
		sb.append("]");
		sb.append("\n");
		sb.append("\n");

		sb.append("verify data density: [");
		for (Sample s : verifyData) {
			if (s.isTestingUserData()) {
				sb.append("|");
			} else {
				sb.append("-");
			}
		}
		sb.append("]");

		return sb.toString();
	}

	private List<Sample> getSamplesFromFile(File file, boolean isTestingUserData) throws IOException {
		BufferedReader reader;

		// prepare raw file dor reading
		reader = new BufferedReader(new FileReader(file));

		// initialize set of samples
		List<Sample> listOfSamples = new ArrayList<>();
		Sample sample = new Sample();

		String line = reader.readLine();
		// for every line of file
		while (line != null) {
			if (line.isEmpty()) {
				// new sample
				listOfSamples.add(sample);
				sample = new Sample();
			} else {
				String[] coordinates = line.split(",");
				// check if there are 3 coordinates
				if (coordinates.length != 3) {
					throw new IllegalStateException("The entry " + line + " has more coordinates than 3");
				}
				sample.add(new double[]{
					Float.parseFloat(coordinates[0]),
					Float.parseFloat(coordinates[1]),
					Float.parseFloat(coordinates[2])
				});
				sample.setTestingUserData(isTestingUserData);
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

}
