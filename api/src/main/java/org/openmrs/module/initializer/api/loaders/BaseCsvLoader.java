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
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.OrderableCsvFile;
import org.openmrs.module.initializer.api.utils.IgnoreBOMInputStream;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.util.CollectionUtils;

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
				
				// getting the lines
				is = new IgnoreBOMInputStream(new FileInputStream(file.getFile()));
				final CsvParser<T, BaseLineProcessor<T>> parser = csvLoader.getParser(is);
				List<String[]> lines = parser.getLines();
				int fileCount = lines.size();
				
				// processing while possible
				int failuresCount = 0;
				while (!CollectionUtils.isEmpty(lines) && failuresCount != lines.size()) {
					log.info("Attempting to process " + lines.size() + " CSV lines that have not been processed yet...");
					failuresCount = lines.size();
					lines = parser.process(lines);
				}
				
				dirUtil.writeChecksum(file.getFile().getName(), file.getChecksum());
				
				// summary logging
				if (CollectionUtils.isEmpty(lines)) {
					log.info(file.getFile().getName() + " ('" + dirUtil.getDomain()
					        + "' domain) was entirely successfully processed.");
					log.info(fileCount + " entities were saved.");
					
				} else {
					final List<CsvLine> csvLines = new ArrayList<>();
					for (String[] line : lines) {
						csvLines.add(new CsvLine(parser.getHeaderLine(), line));
					}
					
					StringBuilder sb = new StringBuilder();
					sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
					sb.append(System.lineSeparator() + "+-+-+-+-- BEGINNING OF CSV FILE ERROR SUMMARY --+-+-+-+");
					sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
					sb.append(System.lineSeparator());
					sb.append(file.getFile().getName() + " ('" + dirUtil.getDomain() + "' domain) was processed and "
					        + lines.size() + " out of " + fileCount + " entities were not saved.");
					sb.append(System.lineSeparator() + "The CSV line(s) corresponding to those entities are listed below:");
					sb.append(Utils.prettyPrint(csvLines));
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator() + "Paste print for spreadsheets... etc:");
					sb.append(System.lineSeparator());
					sb.append(Utils.pastePrint(csvLines));
					sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
					sb.append(System.lineSeparator() + "+-+-+-+-- END OF CSV FILE ERROR SUMMARY --+-+-+-+");
					sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
					log.error(sb.toString());
				}
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
