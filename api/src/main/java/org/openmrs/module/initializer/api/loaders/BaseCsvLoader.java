package org.openmrs.module.initializer.api.loaders;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
	 * By default all CSV loaders do not allow pre-loaders to throw exceptions in their pre-loading
	 * phase.
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
		List<String[]> remainingLines = parser.getLines();
		int totalCount = remainingLines.size();
		
		int lastFailCount = 0;
		CsvFailingLines result = new CsvFailingLines();
		while (!isEmpty(remainingLines) && lastFailCount != remainingLines.size()) {
			log.info("Attempting to process " + remainingLines.size() + " CSV lines that have not been processed yet...");
			lastFailCount = remainingLines.size();
			result = parser.process(remainingLines);
			remainingLines = result.getFailingLines();
		}
		
		//
		// logging
		//
		
		final File file = getLoadedFile();
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
