package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.initializer.api.utils.IgnoreBOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orderable wrapper for CSV {@link File} objects.
 */
public class OrderableCsvFile implements Comparable<OrderableCsvFile> {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected Integer order;
	
	protected String checksum;
	
	protected File file;
	
	public OrderableCsvFile(File file, String checksum) {
		this.checksum = checksum;
		this.file = file;
		
		InputStream is = null;
		String[] headerLine;
		try {
			is = new IgnoreBOMInputStream(new FileInputStream(file));
			headerLine = CsvParser.getHeaderLine(is);
			this.order = BaseLineProcessor.getOrder(headerLine);
		}
		catch (Exception e) {
			log.error("There was an error while attempting to process the header line for file: " + file.getPath(), e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	public Integer getOrder() {
		return order == null ? Integer.MAX_VALUE : order;
	}
	
	public String getChecksum() {
		return checksum;
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	public int compareTo(OrderableCsvFile that) {
		return this.getOrder().compareTo(that.getOrder());
	}
}
