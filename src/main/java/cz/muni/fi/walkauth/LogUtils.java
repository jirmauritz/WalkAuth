package cz.muni.fi.walkauth;

import java.util.Locale;
import java.util.StringJoiner;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

/**
 * Purpose of this class is solely to provide means of logging the learning
 * process and its outcomes.
 *
 * @author Jaroslav Cechak
 */
public class LogUtils {

    public static final String DELIMITER = ";";

    /**
     * Logger for logging traning progress
     */
    public static final Logger LEARNING_LOGGER = Logger.getLogger("learning.csv");
    /**
     * Logger for logging traning outcomes
     */
    public static final Logger WEIGHTS_LOGGER = Logger.getLogger("weights.csv");

    /**
     * Prints learing progress csv file header.
     */
    public static void printLearningHeader() {
        // rollover file so that new csv file is created
        ((RollingFileAppender) LEARNING_LOGGER.getAppender("learning.csv")).rollOver();

        StringJoiner sj = new StringJoiner(DELIMITER);
        sj.add("iteration")
                .add("validation error").add("validation RMSE").add("validation accuracy").add("validation precision").add("validation recall").add("validation F1")
                .add("training error").add("training RMSE").add("training accuracy").add("training precision").add("training recall").add("training F1");

        // print header of csv
        LEARNING_LOGGER.info(sj.toString());

    }

    /**
     * Prints learning progress into log file
     *
     * @param values data to be printed
     */
    public static void logLearning(double[] values) {
        // concatenate all the information into one line separted with commas
        StringJoiner sj = new StringJoiner(DELIMITER);

        Locale.setDefault(Locale.ENGLISH);
        
        for (double value : values) {
            sj.add(String.format("%.4f", value));
        }

        LEARNING_LOGGER.info(sj.toString());
    }

    /**
     * Prints header of a csv file with weights
     *
     * @param n neural network which weights will be printed
     */
    public static void printWeightsHeader(NeuralNetwork n) {
        // rollover file so that new csv file is created
        ((RollingFileAppender) WEIGHTS_LOGGER.getAppender("weights.csv")).rollOver();

        StringJoiner sj = new StringJoiner(DELIMITER);

        for (int i = 1; i <= n.getWeights()[0].getRowCount(); i++) {
            sj.add("x" + i).add("y" + i).add("z" + i);
        }

        // print header of csv
        WEIGHTS_LOGGER.info(sj.toString());

    }

    /**
     * Prints weights into log file
     *
     * @param n neural network with weights that will be printed
     */
    public static void logWeights(NeuralNetwork n) {
        Matrix weights = n.getWeights()[0];

        // probably tests are running not the main run
        if (weights.getColCount() < 4 || weights.getColCount() % 3 != 1) {
            return;
        }

        Locale.setDefault(Locale.ENGLISH);
        
        for (int i = 1; i < weights.getColCount(); i += 3) {

            StringJoiner sj = new StringJoiner(DELIMITER);

            for (int j = 0; j < weights.getRowCount(); j++) {
                sj.add(String.format("%.4f", weights.get(j, i)))
                        .add(String.format("%.4f", weights.get(j, i + 1)))
                        .add(String.format("%.4f", weights.get(j, i + 2)));
            }
            WEIGHTS_LOGGER.info(sj.toString());
        }
    }

}
