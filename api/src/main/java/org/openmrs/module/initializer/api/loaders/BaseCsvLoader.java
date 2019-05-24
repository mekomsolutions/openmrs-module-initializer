package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.OrderableCsvFile;

/**
 * All CSV loaders should subclass the base CSV loader. This class takes care of loading and sorting
 * all CSV files configured through a {@link ConfigDirUtil} instance.
 * 
 * @param <T>
 */
public abstract class BaseCsvLoader<T extends BaseOpenmrsObject, P extends CsvParser<T, BaseLineProcessor<T>>> extends BaseLoader implements CsvLoader/* <P> */ {
	
	protected P parser;
	
	@Override
	public void load() {
		loadCsvFiles(getDirUtil(), this);
	}
	
	@Override
	protected Domain getDomain() {
		return parser.getDomain();
	}
	
	@Override
	public CsvParser<T, BaseLineProcessor<T>> getParser(InputStream is) throws IOException {
		parser.setInputStream(is);
		return parser;
	}
	
	public void loadCsvFiles(ConfigDirUtil dirUtil, CsvLoader csvLoader) {
		
		// Selecting the files that havent' been checksum'd yet
		List<OrderableCsvFile> files = new ArrayList<OrderableCsvFile>();
		for (File file : dirUtil.getFiles("csv")) {
			String fileName = dirUtil.getFileName(file.getPath());
			String checksum = dirUtil.getChecksumIfChanged(fileName);
			if (!checksum.isEmpty()) {
				files.add(new OrderableCsvFile(file, checksum));
			}
		}
		
		Collections.sort(files); // sorting based on the CSV order metadata
		
		// parsing the CSV files
		for (OrderableCsvFile file : files) {
			InputStream is = null;
			try {
				
				is = new FileInputStream(file.getFile());
				final CsvParser<T, BaseLineProcessor<T>> parser = csvLoader.getParser(is);
				
				List<T> failures = parser.saveAll();
				
				int count = 0;
				while (count != failures.size()) {
					log.info("Attempting to save again " + failures.size() + " previously failed entities...");
					count = failures.size();
					failures = parser.save(failures);
				}
				
				dirUtil.writeChecksum(file.getFile().getName(), file.getChecksum());
				log.info("The following '" + dirUtil.getDomain() + "' config file was succesfully processed: "
				        + file.getFile().getName());
				
			}
			catch (IOException e) {
				log.error("Could not parse the '" + dirUtil.getDomain() + "' config file: " + file.getFile().getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
	
}
