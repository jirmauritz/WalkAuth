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
	}

	private static Properties setProperties() throws IOException {
		FileInputStream input = new FileInputStream(PROPERTIES_FILENAME);		
		Properties prop = new Properties();
		prop.load(input);
		return prop;
	}
}
