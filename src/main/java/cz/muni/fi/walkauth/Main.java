package cz.muni.fi.walkauth;

import cz.muni.fi.walkauth.preprocessing.DataManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 *
 * @author Jiri Mauritz : jirmauritz at gmail dot com
 */
public class Main {

    /**
     * Path to properties resource (not the physical file)
     */
    private static final String PROPERTIES_FILENAME = "/config.properties";

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        CommandLine cmd;
        
        try {
            cmd = new DefaultParser().parse(getCommandlineOptions(), args);
        } catch (ParseException pe) {
            // some option has not been recognized
            System.err.println(pe.getMessage());
            printHelp();
            return;
        }

        // print help and halt
        if (cmd.hasOption("h")) {
            printHelp();
            return;
        }

        // properties
        Properties prop = setCommandlineProperties(cmd, setProperties());

        // prepare data
        DataManager dataManager = new DataManager();
        dataManager.prepareData(
                Integer.parseInt(prop.getProperty("entriesPerSample")),
                prop.getProperty("dataPath"),
                prop.getProperty("positiveUser"),
                Float.parseFloat(prop.getProperty("positiveDataRatio")),
                Float.parseFloat(prop.getProperty("trainDataRatio")),
                Float.parseFloat(prop.getProperty("testDataRatio")),
                Float.parseFloat(prop.getProperty("validationDataRatio"))
        );
        System.out.println(dataManager.dataOverview());

        // build topology
        int[] topology = buildTopology(dataManager.getTrainingData()[0].getEntries().length,
                stringToIntArray(prop.getProperty("hiddenNeuronsTopology")), 1);

        // train
        NeuralNetwork network = NeuralNetworkLearning.trainNeuralNetwork(
                topology,
                dataManager.getTrainingData(),
                dataManager.getValidationData(),
                Float.parseFloat(prop.getProperty("acceptableError")),
                (Integer iteration, Double error) -> {
                    double speed = Double.parseDouble(prop.getProperty("learningSpeed"));
                    return speed * error / ((iteration + 8)/8.0); 
                },
                Integer.parseInt(prop.getProperty("maxIterations"))
        );

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
        InputStream input = Main.class.getResourceAsStream(PROPERTIES_FILENAME);
        Properties prop = new Properties();
        prop.load(input);
        return prop;
    }

    private static int[] stringToIntArray(String string) {
        String withoutBrackets = string.substring(1, string.length() - 1);
        String[] stringArray = withoutBrackets.split(",");
        int[] array = new int[stringArray.length];
        try {
            for (int i = 0; i < stringArray.length; i++) {
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
            layers[i + 1] = hiddenLayers[i];
        }
        layers[hiddenLayers.length + 1] = outputs;
        return layers;
    }

    /**
     * Defines all possible commandline parameters and switches.
     *
     * @return object with all the commandline options
     */
    private static Options getCommandlineOptions() {
        Options cmdOptions = new Options();

        cmdOptions.addOption(Option.builder("e")
                .longOpt("entries-per-sample")
                .argName("N")
                .hasArg()
                .desc("Defines how many entires (triplets of derivations) are considered as one sample. In other words, it says how long sequences are being classified.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("u")
                .longOpt("positive-user")
                .argName("FILE")
                .hasArg()
                .desc("Chooses CSV file with the person whose walk the neural network will try to learn.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("r")
                .longOpt("train-data-ratio")
                .argName("RATIO")
                .hasArg()
                .desc("Defines the portion of available data used for training in gradient descent.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("t")
                .longOpt("test-data-ratio")
                .argName("RATIO")
                .hasArg()
                .desc("Defines the portion of available data used for final test.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("v")
                .longOpt("validation-data-ratio")
                .argName("RATIO")
                .hasArg()
                .desc("Defines the portion of available data use for validation in gradient descent.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("n")
                .longOpt("hidden-neurons-topology")
                .argName("[N,M,...]")
                .hasArg()
                .desc("Defines hidden neuron layers. Expected format is sequence of numbers inside square brackets separated by commas. You need to encolse the whole list in commas if you wish to put spaces beween items. Examples: [30,10]")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("a")
                .longOpt("acceptable-error")
                .argName("ERROR")
                .hasArg()
                .desc("The threshold for an error on validation data that, when surpassed, the learning will end.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("s")
                .longOpt("learning-speed")
                .argName("SPEED")
                .hasArg()
                .desc("The values that is used as base value in computing learning speed.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("i")
                .longOpt("max-iterations")
                .argName("N")
                .hasArg()
                .desc("The upper bound on number of gradient descends there will be in learning.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Prints this help message.")
                .required(false)
                .build()
        );
        cmdOptions.addOption(Option.builder("d")
                .longOpt("path-to-data")
                .argName("PATH")
                .hasArg()
                .desc("Defines where the program will look for raw walking data.")
                .required(false)
                .build()
        );

        return cmdOptions;
    }

    /**
     * Prints help message.
     */
    private static void printHelp() {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(Integer.MAX_VALUE);
        hf.setLeftPadding(4);
        hf.setDescPadding(4);
        hf.printHelp("java -jar WalkAuth.jar", getCommandlineOptions());
    }

    /**
     * Overwrites those properties that the user manually inputed in the
     * commandline.
     *
     * @param cmd object with parsed commandline parameters
     * @param prop properties to be overwriten
     * @return merged properties
     */
    private static Properties setCommandlineProperties(CommandLine cmd, Properties prop) {
        for (Option o : cmd.getOptions()) {
            prop.setProperty(optionToPropertyName(o.getOpt()), o.getValue());
        }
        return prop;
    }

    /**
     * Provides transalation between short commandline options and properties
     * names.
     *
     * @param optName option name to be transalted
     * @return corresponding property name
     */
    private static String optionToPropertyName(String optName) {
        switch (optName) {
            case "e":
                return "entriesPerSample";
            case "d":
                return "dataPath";
            case "u":
                return "positiveUser";
            case "r":
                return "trainDataRatio";
            case "t":
                return "testDataRatio";
            case "v":
                return "validationDataRatio";
            case "n":
                return "hiddenNeuronsTopology";
            case "a":
                return "acceptableError";
            case "s":
                return "learningSpeed";
            case "i":
                return "maxIterations";
            default:
                return null;
        }
    }
}
