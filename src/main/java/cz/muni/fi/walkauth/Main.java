package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.DataManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 *
 * @author Jiri Mauritz : jirmauritz at gmail dot com
 */
public class Main {

	private static final String PROPERTIES_FILENAME = "src/main/resources/config.properties";

	/**
	 * @param args the command line arguments
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception {
		// properties
		Properties prop = setProperties();
		
		// prepare data
		DataManager dataManager = new DataManager();
		dataManager.prepareData(
				Integer.parseInt(prop.getProperty("entriesPerSample")), 
				prop.getProperty("dataPath"),
				prop.getProperty("testingUser"),
				Float.parseFloat(prop.getProperty("trainDataRatio")),
				Float.parseFloat(prop.getProperty("testDataRatio")),
				Float.parseFloat(prop.getProperty("verifyDataRatio"))
		);
		System.out.println(dataManager.dataOverview());
		
		// build topology
		int[] topology = buildTopology(dataManager.getTrainingData()[0].getEntries().length,
				stringToIntArray(prop.getProperty("hiddenNeuronsTopology")),
				1);
		
		// train
		NeuralNetwork network = NeuralNetworkLearning.trainNeuralNetwork(
				topology,
				dataManager.getTrainingData(), 
				dataManager.getValidationData(), 
				Float.parseFloat(prop.getProperty("acceptableError")),                      
             
				(Integer iteration, Double error) -> {
                                    double speed = Double.parseDouble(prop.getProperty("learningSpeed"));
                                    return speed * error / iteration;
                                },
				Integer.parseInt(prop.getProperty("maxIterations")));
		
		// evaluate
		double error = Evaluation.computeError(network, dataManager.getTestingData());
		double rmse = Evaluation.computeRMSE(network, dataManager.getTestingData());
		double accuracy = Evaluation.computeAccuracy(network, dataManager.getTestingData());
        double precision = Evaluation.computePrecision(network, dataManager.getTestingData());
        double recall = Evaluation.computeRecall(network, dataManager.getTestingData());
        double f1 = Evaluation.computeF1(network, dataManager.getTestingData());
		
		System.out.println("Error: " + error);
		System.out.println("RMSE: " + rmse);
		System.out.println("Accuracy: " + accuracy);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + f1);
	}

	private static Properties setProperties() throws IOException {
		FileInputStream input = new FileInputStream(PROPERTIES_FILENAME);		
		Properties prop = new Properties();
		prop.load(input);
		return prop;
	}
	
	private static int[] stringToIntArray(String string) {
		String withoutBrackets = string.substring(1, string.length() - 1);
		String[] stringArray = withoutBrackets.split(",");
		int[] array = new int[stringArray.length];
		try {
		for (int i = 0; i < stringArray.length; i ++) {
			array[i] = Integer.parseInt(stringArray[i].trim());
		}
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Wrong syntax in config file in the hiddenNeuronsTopology field.", ex);
		}
		return array;
	}
	
	private static int[] buildTopology(int inputs, int[] hiddenLayers, int outputs) {
		int[] layers = new int[hiddenLayers.length + 2];
		layers[0] = inputs;
		for (int i = 0; i < hiddenLayers.length; i++) {
			layers[i+1] = hiddenLayers[i];
		}
		layers[hiddenLayers.length + 1] = outputs;
		return layers;
	}
}
