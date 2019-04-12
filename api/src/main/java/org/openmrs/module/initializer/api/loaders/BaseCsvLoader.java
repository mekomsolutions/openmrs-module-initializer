package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.OrderableCsvFile;

/**
 * All CSV loaders should subclass the base CSV loader. This class takes care of loading and sorting
 * all CSV files configured through a {@link ConfigDirUtil} instance.
 */
public abstract class BaseCsvLoader extends BaseLoader implements CsvLoader {
	
	@Override
	public void load() {
		loadCsvFiles(getDirUtil(), this);
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
				
				csvLoader.getParser(is).saveAll();
				
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
