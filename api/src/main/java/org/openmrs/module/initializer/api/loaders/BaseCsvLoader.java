package org.openmrs.module.initializer.api.loaders;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.CsvFailingLines;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.OrderedCsvFile;
import org.openmrs.module.initializer.api.OrderedFile;
import org.openmrs.module.initializer.api.c.LocalizedHeader;
import org.openmrs.module.initializer.api.utils.Utils;

/**
 * All CSV loaders should subclass the base CSV loader. This class takes care of loading and sorting
 * all CSV files configured through a {@link ConfigDirUtil} instance.
 * 
 * @param <T>
 */
public abstract class BaseCsvLoader<T extends BaseOpenmrsObject, P extends CsvParser<T, BaseLineProcessor<T>>> extends BaseI18nSupportLoader implements CsvLoader/* <P> */ {
	
	protected P parser;
	
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
	public OrderedFile newOrderedFile(File file) {
		return new OrderedCsvFile(file);
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
				log.warn("An OpenMRS object could not be constructed or saved from the following CSV line:"
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
	
	@Override
	public void loadI18nMessages(InputStream is) throws Exception {
		final CsvParser<T, BaseLineProcessor<T>> parser = getParser(is);
		
		// load the i18n messages
		String[] headerLine = parser.getHeaderLine();
		
		for (String[] line : parser.getLines()) {
			final CsvLine csvLine = new CsvLine(headerLine, line);
			LocalizedHeader lh = LocalizedHeader.getLocalizedHeader(csvLine.getHeaderLine(),
			    BaseLineProcessor.HEADER_DISPLAY);
			for (Locale displayLocale : lh.getLocales()) {
				String display = csvLine.get(lh.getI18nHeader(displayLocale));
				if (!StringUtils.isEmpty(display)) {
					T target = parser.bootstrap(csvLine);
					if (target.getId() != null) {
						String[] codes = getLocalizationCodes(target.getClass().getSimpleName(), target.getUuid());
						initializerMessageSource
						        .addPresentation(new PresentationMessage(codes[0], displayLocale, display, null));
						initializerMessageSource
						        .addPresentation(new PresentationMessage(codes[1], displayLocale, display, null));
					}
				}
			}
		}
	}
	
	private String[] getLocalizationCodes(String shortClassName, String uuid) {
		// in case this is a hibernate-proxy or javaassist object, strip off anything after an underscore
		// ie: EncounterType$HibernateProxy$ODcBnusu/EncounterType_$$_javassist_26 needs to be converted to EncounterType
		
		int underscoreIndex = shortClassName.indexOf("_$");
		if (underscoreIndex > 0) {
			shortClassName = shortClassName.substring(0, underscoreIndex);
		} else {
			underscoreIndex = shortClassName.indexOf("$");
			if (underscoreIndex > 0) {
				shortClassName = shortClassName.substring(0, underscoreIndex);
			}
		}
		String[] codes = new String[2];
		codes[0] = "ui.i18n." + shortClassName + ".name." + uuid;
		codes[1] = "org.openmrs." + shortClassName + "." + uuid;
		return codes;
	}
}
