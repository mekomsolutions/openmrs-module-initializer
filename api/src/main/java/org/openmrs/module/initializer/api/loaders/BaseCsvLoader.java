package org.openmrs.module.initializer.api.loaders;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.CsvFailingLines;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.OrderedCsvFile;
import org.openmrs.module.initializer.api.OrderedFile;
import org.openmrs.module.initializer.api.display.DisplaysPreLoader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * All CSV loaders should subclass the base CSV loader. This class takes care of loading and sorting
 * all CSV files configured through a {@link ConfigDirUtil} instance.
 * 
 * @param <T> A subclass of OpenmrsObject
 * @param <P> A subclass of CsvParser
 */
public abstract class BaseCsvLoader<T extends OpenmrsObject, P extends CsvParser<T, BaseLineProcessor<T>>> extends BaseInputStreamLoader implements CsvLoader<P> {
	
	protected P parser;
	
	private DisplaysPreLoader displaysLoader;
	
	/**
	 * @param displaysLoader
	 */
	@Autowired
	public void setDisplaysPreLoader(DisplaysPreLoader displaysLoader) {
		this.displaysLoader = displaysLoader;
	}
	
	@Override
	protected Domain getDomain() {
		return parser.getDomain();
	}
	
	@Override
	protected String getFileExtension() {
		return "csv";
	}
	
	@Override
	public CsvParser<T, BaseLineProcessor<T>> getParser(InputStream is) throws IOException {
		parser.setInputStream(is);
		return parser;
	}
	
	@Override
	public OrderedFile toOrderedFile(File file) {
		return new OrderedCsvFile(file);
	}
	
	/**
	 * By default all CSV loaders run the displays pre-loader in their pre-loading phase.
	 */
	@Override
	protected void preload(final File file) throws Exception {
		displaysLoader.setBootstrapParser(parser);
		displaysLoader.load(file);
	}
	
	/**
	 * By default CSV loaders do not allow their pre-loader to throw.
	 */
	@Override
	protected boolean throwingOnPreload(boolean doThrow) {
		return false;
	}
	
	@Override
	protected void load(InputStream is) throws Exception {
		
		//
		// processing while possible
		//
		
		final CsvParser<T, BaseLineProcessor<T>> parser = getParser(is);
		final List<String[]> allLines = parser.getLines();
		final int totalCount = allLines.size();
		final String[] headerLine = parser.getHeaderLine();
		
		final boolean rowChecksumsEnabled = cfg.isRowChecksumsEnabled() && !cfg.skipChecksums();
		final File file = getLoadedFile();
		final ConfigDirUtil dirUtil = getDirUtil();
		
		final Set<String> previousRowHashes = rowChecksumsEnabled ? dirUtil.readRowChecksums(file) : new HashSet<>();
		
		List<String[]> remainingLines;
		if (rowChecksumsEnabled) {
			remainingLines = new ArrayList<>();
			for (String[] line : allLines) {
				if (!previousRowHashes.contains(ConfigDirUtil.computeRowChecksum(headerLine, line))) {
					remainingLines.add(line);
				}
			}
			int skipped = totalCount - remainingLines.size();
			if (skipped > 0) {
				log.info(skipped + " of " + totalCount + " CSV rows in " + file.getName()
				        + " are unchanged since last load and will be skipped.");
			}
		} else {
			remainingLines = new ArrayList<>(allLines);
		}
		
		int lastFailCount = 0;
		CsvFailingLines result = new CsvFailingLines();
		while (!isEmpty(remainingLines) && lastFailCount != remainingLines.size()) {
			log.info("Attempting to process " + remainingLines.size() + " CSV lines that have not been processed yet...");
			lastFailCount = remainingLines.size();
			result = parser.process(remainingLines);
			remainingLines = result.getFailingLines();
		}
		
		// Row checksum bookkeeping: persist hashes for rows considered processed — i.e. all rows in
		// the file except those that failed on this run. Failing rows are excluded so they will be
		// retried on the next load.
		if (rowChecksumsEnabled) {
			Set<String> failingHashes = new HashSet<>();
			for (String[] failingLine : result.getFailingLines()) {
				failingHashes.add(ConfigDirUtil.computeRowChecksum(headerLine, failingLine));
			}
			Set<String> newRowHashes = new HashSet<>();
			for (String[] line : allLines) {
				String rowHash = ConfigDirUtil.computeRowChecksum(headerLine, line);
				if (!failingHashes.contains(rowHash)) {
					newRowHashes.add(rowHash);
				}
			}
			dirUtil.writeRowChecksums(file, newRowHashes);
		}
		
		//
		// logging
		//
		
		// success logging
		if (isEmpty(result.getFailingLines())) {
			log.info(file.getName() + " ('" + getDomainName() + "' domain) was entirely successfully processed.");
			log.info(totalCount + " entities were saved.");
			return;
		}
		// logging the exception stack traces collected during CSV processing
		else {
			result.getErrorDetails().forEach(ed -> {
				log.error("An OpenMRS object could not be constructed or saved from the following CSV line:"
				        + ed.getCsvLine().prettyPrint(),
				    ed.getException());
			});
		}
		
		//
		// throwing (error summary)
		//
		final List<CsvLine> errLines = result.getErrorDetails().stream().map(ed -> ed.getCsvLine())
		        .collect(Collectors.toList());
		StringBuilder sb = new StringBuilder();
		sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		sb.append(System.lineSeparator() + "+-+-+-+-- BEGINNING OF CSV FILE ERROR SUMMARY --+-+-+-+");
		sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		sb.append(System.lineSeparator());
		sb.append(file.getName() + " ('" + getDomainName() + "' domain) was processed and " + errLines.size() + " out of "
		        + totalCount + " entities were not saved.");
		sb.append(System.lineSeparator() + "The CSV line(s) corresponding to those entities are listed below:");
		sb.append(Utils.prettyPrint(errLines));
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator() + "Paste print for spreadsheets... etc:");
		sb.append(System.lineSeparator());
		sb.append(Utils.pastePrint(errLines));
		sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		sb.append(System.lineSeparator() + "+-+-+-+-+--  END OF CSV FILE ERROR SUMMARY  --+-+-+-+-+");
		sb.append(System.lineSeparator() + "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		
		throw new IllegalArgumentException(sb.toString());
		
	}
}
