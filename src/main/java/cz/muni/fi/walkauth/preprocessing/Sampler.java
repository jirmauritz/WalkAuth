package cz.muni.fi.walkauth.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;

/**
 * Class divides raw data in samples which are going to be units of machine
 * learning.
 *
 * @author Jiri Mauritz: jirmauritz at gmail dot com
 */
public class Sampler {
	
	private final String RAW_DIR = "data/raw/";
	private final String SAMPLES_DIR = "data/samples/";

	
	/**
	 * Split raw data into samples, which are stored in data/samples/ in the same named files.
	 * The sample is specified number of lines divided by a new line.
	 * 
	 * @param entriesForSample - parameter defining how many entries (lines) is one sample
	 * @throws IOException when the data/raw/ directory does not exist or you do not have permissions
	 */
	public void sample(int entriesForSample) throws IOException {
		// raw data path
		File rawDirectory = new File(RAW_DIR);
		// check if raw directory exists
		if (!rawDirectory.canRead()) {
			throw new IOException("Cannot read the directory of raw data " + RAW_DIR);
		}
		
		// sample directory path
		File sampleDirectory = new File(SAMPLES_DIR);
		Files.createDirectories(sampleDirectory.toPath());

		BufferedReader reader;
		PrintWriter writer;

		// for every file
		for (final File file : rawDirectory.listFiles()) {
			// prepare raw file dor reading
			reader = new BufferedReader(new FileReader(file));

			// count entries so that in every sample is only specified number of entries
			int entry = 1;
			
			// prepare sampled file
			writer = new PrintWriter(SAMPLES_DIR + file.getName(), "UTF-8");

			String line = reader.readLine();
			// for every line of file
			while (line != null) {
				if (entry % entriesForSample == 0) {
					// next sample
					writer.println();
				}
				writer.println(line);
				entry++;
				line = reader.readLine();
			}
			// close last sample
			writer.close();
		}
	}
	
	public void clearSamples() throws IOException {
		// sample directory path
		File sampleDirectory = new File(SAMPLES_DIR);
		
		if (!sampleDirectory.exists()) {
			// the sample directory does not even exist, no big deal
			return;
		}
		
		if (!sampleDirectory.canWrite()) {
			// no permissions to delete the sample directory
			throw new IOException("Cannot delete sample directory: no permissions.");
		}
		
		// delete it
		FileUtils.deleteDirectory(sampleDirectory);
	}

}
