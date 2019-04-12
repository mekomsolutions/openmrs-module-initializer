package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.module.initializer.api.CsvParser;

/**
 * All CSV loaders should implement this interface, @see {@link BaseCsvLoader}
 */
public interface CsvLoader extends Loader {
	
	/**
	 * @return A parser built on the provided CSV file as input stream.
	 */
	@SuppressWarnings("rawtypes")
	CsvParser getParser(InputStream is) throws IOException;
	
}
