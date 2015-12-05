package cz.muni.fi.walkauth;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

/**
 * Purpose of this class is solely to provide means of logging the learning
 * process and its outcomes.
 *
 * @author Jaroslav Cechak
 */
public class LogUtils {

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
        // print header of csv
        LEARNING_LOGGER.info("iteration,validation error,validation RMSE,validation accuracy,validation precision,validation recall,validation F1,training error,training RMSE,training accuracy,training precision,training recall,training F1");

    }

    /**
     * Prints learning progress into log file
     *
     * @param message data to be printed
     */
    public static void logLearning(double[] message) {
        // concatenate all the information into one line separted with commas
        StringBuilder sb = new StringBuilder();
        if (message.length >= 1) {
            sb.append(String.format("%.4f", message[0]));
        }
        for (int i = 1; i < message.length; i++) {
            sb.append(",").append(message[i]);
        }

        LEARNING_LOGGER.info(sb.toString());
    }

    /**
     * Prints header of a csv file with weights
     *
     * @param n neural network which weights will be printed
     */
    public static void printWeightsHeader(NeuralNetwork n) {
        // rollover file so that new csv file is created
        ((RollingFileAppender) WEIGHTS_LOGGER.getAppender("weights.csv")).rollOver();

        StringBuilder sb = new StringBuilder("x1, y1, z1");

        for (int i = 1; i <= n.getWeights()[0].getRowCount(); i++) {
            sb.append(",x").append(i).append(" ,y").append(i).append(",z").append(i);
        }

        // print header of csv
        WEIGHTS_LOGGER.info(sb.toString());

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

        for (int i = 1; i < weights.getColCount(); i += 3) {

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%.4f", weights.get(0, i)))
                    .append(",")
                    .append(String.format("%.4f", weights.get(0, i + 1)))
                    .append(",")
                    .append(String.format("%.4f", weights.get(0, i + 2)));

            for (int j = 1; j < weights.getRowCount(); j++) {
                sb.append(",")
                        .append(String.format("%.4f", weights.get(j, i)))
                        .append(",")
                        .append(String.format("%.4f", weights.get(j, i + 1)))
                        .append(",")
                        .append(String.format("%.4f", weights.get(j, i + 2)));
            }
            WEIGHTS_LOGGER.info(sb.toString());
        }
    }

}
