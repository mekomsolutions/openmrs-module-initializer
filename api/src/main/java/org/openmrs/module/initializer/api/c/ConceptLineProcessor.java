package org.openmrs.module.initializer.api.c;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
@Component("initializer.conceptLineProcessor")
public class ConceptLineProcessor extends BaseLineProcessor<Concept> {
	
	protected static String HEADER_SHORTNAME = "short name";
	
	protected static String HEADER_FSNAME = "fully specified name";
	
	protected static String HEADER_CLASS = "data class";
	
	protected static String HEADER_DATATYPE = "data type";
	
	protected ConceptService conceptService;
	
	@Autowired
	public ConceptLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	protected Concept bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Concept concept = conceptService.getConceptByUuid(uuid);
		
		if (StringUtils.isEmpty(uuid) && concept == null) {
			Locale currentLocale = Context.getLocale();
			LocalizedHeader lh = getLocalizedHeader(HEADER_FSNAME);
			for (Locale nameLocale : lh.getLocales()) {
				String name = line.get(lh.getI18nHeader(nameLocale));
				if (!StringUtils.isEmpty(name)) {
					Context.setLocale(nameLocale);
					concept = conceptService.getConceptByName(name);
					if (concept != null) {
						break;
					}
				}
			}
			Context.setLocale(currentLocale);
		}
		
		if (concept == null) {
			concept = new Concept();
			if (!StringUtils.isEmpty(uuid)) {
				concept.setUuid(uuid);
			}
		}
		
		return concept;
	}
	
	/*
	 * This is the base concept implementation.
	 */
	protected Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		LocalizedHeader lh = null;
		
		// Clearing existing names
		for (ConceptName cn : concept.getNames()) {
			concept.removeName(cn);
		}
		
		// Fully specified names
		lh = getLocalizedHeader(HEADER_FSNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(name)) {
				ConceptName conceptName = new ConceptName(name, locale);
				concept.setFullySpecifiedName(conceptName);
			}
		}
		
		// Short names
		lh = getLocalizedHeader(HEADER_SHORTNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(name)) {
				ConceptName conceptName = new ConceptName(name, locale);
				concept.setShortName(conceptName);
			}
		}
		
		// Descriptions
		if (!CollectionUtils.isEmpty(concept.getDescriptions())) {
			concept.getDescriptions().clear();
		}
		lh = getLocalizedHeader(HEADER_DESC);
		for (Locale locale : lh.getLocales()) {
			String desc = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(desc)) {
				ConceptDescription conceptDesc = new ConceptDescription(desc, locale);
				concept.addDescription(conceptDesc);
			}
		}
		
		// Concept data class
		String conceptClassName = line.getString(HEADER_CLASS, "");
		ConceptClass conceptClass = conceptService.getConceptClassByName(conceptClassName);
		concept.setConceptClass(conceptClass);
		
		// Concept data type
		String conceptTypeName = line.getString(HEADER_DATATYPE, "");
		ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptTypeName);
		concept.setDatatype(conceptDatatype);
		
		return concept;
	}
}
