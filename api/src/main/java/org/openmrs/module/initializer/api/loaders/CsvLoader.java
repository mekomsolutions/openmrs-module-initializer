package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.module.initializer.api.CsvParser;

/**
 * All CSV loaders should implement this interface, @see {@link BaseCsvLoader}
 */
@SuppressWarnings("rawtypes")
public interface CsvLoader/* <P extends CsvParser> */extends Loader {
	
	/**
	 * @return The domain parser built on the provided CSV file as input stream.
	 */
	CsvParser getParser(InputStream is) throws IOException;
	
	/**
	 * Sets the domain parser bean for this CSV loader.
	 * 
	 * @param parser A Spring bean CSV parser.
	 */
	// void setParser(P parser);
	
}
