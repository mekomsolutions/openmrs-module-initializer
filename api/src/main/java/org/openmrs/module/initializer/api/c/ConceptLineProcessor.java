package org.openmrs.module.initializer.api.c;

import static org.openmrs.module.initializer.api.c.LocalizedHeader.getLocalizedHeader;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
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
	
	final public static String HEADER_SHORTNAME = "short name";
	
	final public static String HEADER_FSNAME = "fully specified name";
	
	final public static String HEADER_CLASS = "data class";
	
	final public static String HEADER_DATATYPE = "data type";
	
	protected ConceptService conceptService;
	
	@Autowired
	public ConceptLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	/*
	 * This is the base concept implementation.
	 */
	@Override
	public Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		LocalizedHeader lh = null;
		
		// Clearing existing names
		for (ConceptName cn : concept.getNames()) {
			concept.removeName(cn);
		}
		
		// Fully specified names
		lh = getLocalizedHeader(line.getHeaderLine(), HEADER_FSNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(name)) {
				String nameUuid = Utils.generateUuidFromObjects(concept.getUuid(), name, ConceptNameType.FULLY_SPECIFIED,
				    locale);
				ConceptName conceptName = getOrCreateConceptName(nameUuid, name, locale);
				concept.setFullySpecifiedName(conceptName);
			}
		}
		
		// Short names
		lh = getLocalizedHeader(line.getHeaderLine(), HEADER_SHORTNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(name)) {
				String nameUuid = Utils.generateUuidFromObjects(concept.getUuid(), name, ConceptNameType.SHORT, locale);
				ConceptName conceptName = getOrCreateConceptName(nameUuid, name, locale);
				concept.setShortName(conceptName);
			}
		}
		
		// Descriptions
		if (!CollectionUtils.isEmpty(concept.getDescriptions())) {
			concept.getDescriptions().clear();
		}
		lh = getLocalizedHeader(line.getHeaderLine(), HEADER_DESC);
		for (Locale locale : lh.getLocales()) {
			String desc = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(desc)) {
				ConceptDescription conceptDesc = new ConceptDescription(desc, locale);
				concept.addDescription(conceptDesc);
			}
		}
		
		// Concept data class
		String conceptClassName = line.getString(HEADER_CLASS, "");
		if (!StringUtils.isEmpty(conceptClassName)) {
			ConceptClass conceptClass = conceptService.getConceptClassByName(conceptClassName);
			if (conceptClass == null) {
				throw new IllegalArgumentException(
				        "Bad concept class name '" + conceptClassName + "' for line:" + line.toString());
			}
			concept.setConceptClass(conceptClass);
		}
		
		// Concept data type
		String conceptTypeName = line.getString(HEADER_DATATYPE, "");
		if (!StringUtils.isEmpty(conceptTypeName)) {
			ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptTypeName);
			if (conceptDatatype == null) {
				throw new IllegalArgumentException(
				        "Bad concept datatype name '" + conceptTypeName + "' for line:" + line.toString());
			}
			concept.setDatatype(conceptDatatype);
		}
		
		return concept;
	}
	
	private ConceptName getOrCreateConceptName(String nameUuid, String name, Locale locale) {
		ConceptName conceptName = conceptService.getConceptNameByUuid(nameUuid);
		if (conceptName == null) {
			conceptName = new ConceptName(name, locale);
			conceptName.setUuid(nameUuid);
		} else {
			conceptName.setVoided(false);
			conceptName.setDateVoided(null);
			conceptName.setVoidedBy(null);
			conceptName.setVoidReason(null);
		}
		return conceptName;
	}
}
