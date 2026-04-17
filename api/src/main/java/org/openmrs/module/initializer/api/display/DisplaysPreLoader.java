package org.openmrs.module.initializer.api.display;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.c.LocalizedHeader;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.initializer.api.utils.IgnoreBOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.openmrs.module.initializer.api.display.DisplayLineProcessor.HEADER_DISPLAY;

@Component
public class DisplaysPreLoader extends BaseCsvLoader<OpenmrsObject, DisplaysCsvParser> {
	
	@Override
	public boolean isPreLoader() {
		return true;
	}
	
	@Override
	public String getDomainName() {
		return "displays";
	}
	
	@Override
	public Integer getOrder() {
		return 0;
	}
	
	@Autowired
	public void setParser(DisplaysCsvParser parser) {
		this.parser = parser;
	}
	
	@Override
	public void setDisplaysPreLoader(DisplaysPreLoader displaysLoader) {
		// this override prevents the parent's method Spring autowiring that would result in a cyclic bean creation error.
	}
	
	public void setBootstrapParser(
	        CsvParser<? extends OpenmrsObject, ? extends BaseLineProcessor<? extends OpenmrsObject>> parser) {
		this.parser.setBootstrapParser(parser);
	}
	
	@Override
	public void load(File file) throws Exception {
		try (InputStream is = new IgnoreBOMInputStream(new FileInputStream(file))) {
			final CsvParser<OpenmrsObject, BaseLineProcessor<OpenmrsObject>> parser = getParser(is);
			LocalizedHeader lh = LocalizedHeader.getLocalizedHeader(parser.getHeaderLine(), HEADER_DISPLAY);
			if (lh != null && !lh.getLocales().isEmpty()) {
				super.load(file);
			}
		}
	}
}
