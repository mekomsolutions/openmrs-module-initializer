package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.initializer.api.utils.IgnoreBOMInputStream;

/**
 * Reads the order of a CSV file as the RHS value of the _order metadata header. Eg. "_order:1000" →
 * order = 1000.
 */
public class OrderedCsvFile extends OrderedFile {
	
	public OrderedCsvFile(File file) {
		super(file.getAbsolutePath());
	}
	
	@Override
	protected Integer fetchOrder(File file) throws Exception {
		String[] headerLine;
		try (InputStream is = new IgnoreBOMInputStream(new FileInputStream(file))) {
			headerLine = CsvParser.getHeaderLine(is);
		}
		return BaseLineProcessor.getOrder(headerLine);
	}
}
