package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.openmrs.module.initializer.api.c.LocalizedHeader.getLocalizedHeader;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
@Component("initializer.conceptLineProcessor")
public class ConceptLineProcessor extends BaseLineProcessor<Concept> {
	
	final public static String HEADER_SHORTNAME = "short name";
	
	final public static String HEADER_FSNAME = "fully specified name";
	
	final public static String HEADER_INDEX_TERM = "index term";
	
	final public static String HEADER_SYNONYM = "synonym";
	
	final public static String HEADER_PREFERRED = "preferred";
	
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
		
		// First handle all Concept Name updates.
		
		// Iterate over headers, and for those that denote a Concept Name, add to a collection to process
		Map<String, ConceptName> conceptNamesToProcess = new LinkedHashMap<>();
		Map<Locale, ConceptName> localePreferredNames = new HashMap<>();
		for (String h : line.getHeaderLine()) {
			String[] headerComponents = h.trim().split(LOCALE_SEPARATOR, 3);
			h = h.toLowerCase().trim();
			if (h.startsWith(HEADER_FSNAME) || h.startsWith(HEADER_SHORTNAME) || h.startsWith(HEADER_SYNONYM)) {
				if (headerComponents.length == 1) {
					throw new IllegalArgumentException("Concept Name Headers must specify a locale as <name>:<locale>");
				} else if (headerComponents.length == 2) {
					Locale locale = LocaleUtils.toLocale(headerComponents[1]);
					String name = line.get(h);
					if (StringUtils.isNotEmpty(name)) {
						ConceptName cn = new ConceptName(name, locale);
						
						ConceptNameType nameType = getConceptNameTypeForHeader(h);
						cn.setConceptNameType(nameType);
						
						Boolean localePreferred = line.getBool(h + LOCALE_SEPARATOR + HEADER_PREFERRED);
						cn.setLocalePreferred(localePreferred == null ? Boolean.FALSE : localePreferred);
						if (cn.getLocalePreferred()) {
							if (localePreferredNames.get(locale) != null) {
								String msg = "Only one name in a locale can be marked as preferred";
								throw new IllegalArgumentException(msg);
							} else {
								localePreferredNames.put(locale, cn);
							}
						} else {
							if (!localePreferredNames.containsKey(locale)) {
								localePreferredNames.put(locale, null);
							}
						}
						
						String uuid = line.get(h + LOCALE_SEPARATOR + HEADER_UUID);
						if (StringUtils.isBlank(uuid)) {
							uuid = generateConceptNameUuid(concept.getUuid(), name, nameType, locale);
						}
						cn.setUuid(uuid);
						
						conceptNamesToProcess.put(uuid, cn);
					}
				}
			}
		}
		
		// Iterate over all of the names to process and set preferred names if necessary
		for (ConceptName conceptName : conceptNamesToProcess.values()) {
			if (localePreferredNames.get(conceptName.getLocale()) == null) {
				ConceptNameType nameType = conceptName.getConceptNameType();
				// Only synonyms and fully specified names are allowed to be preferred
				if (nameType == null || nameType == ConceptNameType.FULLY_SPECIFIED) {
					conceptName.setLocalePreferred(true);
					localePreferredNames.put(conceptName.getLocale(), conceptName);
				}
			}
		}
		
		// Update the concept with the constructed Concept Names
		
		// First, remove or update any existing Concept Names
		for (ConceptName existingName : concept.getNames(true)) {
			ConceptName newName = conceptNamesToProcess.get(existingName.getUuid());
			if (newName != null) {
				
				// If this is a change to the name, fail.  The ConceptService has checks in place to determine if a
				// ConceptName.name has changed, and to automatically void and assign a new random name uuid
				// If we allow this, it would mean that a uuid specified in the CSV would not reflect the saved UUID
				// It would also mean that any UUID generation algorithm introduced for ConceptNames would not be respected
				// We want to prevent this behavior.  Users should instead explicitly void and recreate Concept Names in their CSVs
				
				if (!existingName.getName().equals(newName.getName())) {
					StringBuilder msg = new StringBuilder();
					msg.append("It is not permitted to change the name property of an existing ConceptName. ");
					msg.append("Users should instead mark the existing Concept Name as voided, and then ");
					msg.append("create a new ConceptName with their name of choice.");
					throw new IllegalArgumentException(msg.toString());
				}
				
				existingName.setConceptNameType(newName.getConceptNameType());
				existingName.setName(newName.getName());
				existingName.setLocale(newName.getLocale());
				existingName.setLocalePreferred(newName.getLocalePreferred());
				conceptNamesToProcess.remove(existingName.getUuid()); // Remove once we have processed it
			} else {
				// We void the name rather than removing it as there may be data associated with it
				existingName.setVoided(true);
				existingName.setDateVoided(new Date());
				existingName.setVoidedBy(Context.getAuthenticatedUser());
				existingName.setVoidReason("Concept.name.voidedByInitializer");
			}
		}
		
		// Now, add in any new Concept Names
		for (ConceptName newName : conceptNamesToProcess.values()) {
			concept.addName(newName);
		}
		
		// Descriptions
		if (!CollectionUtils.isEmpty(concept.getDescriptions())) {
			concept.getDescriptions().clear();
		}
		LocalizedHeader lh = getLocalizedHeader(line.getHeaderLine(), HEADER_DESC);
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
	
	/**
	 * @return the ConceptNameType represented by a given ConceptName component header, or null if
	 *         header does not represent a component of a ConceptName
	 */
	protected ConceptNameType getConceptNameTypeForHeader(String header) {
		if (header.startsWith(HEADER_FSNAME)) {
			return ConceptNameType.FULLY_SPECIFIED;
		} else if (header.startsWith(HEADER_SHORTNAME)) {
			return ConceptNameType.SHORT;
		} else if (header.startsWith(HEADER_INDEX_TERM)) {
			return ConceptNameType.INDEX_TERM;
		} else if (header.startsWith(HEADER_SYNONYM)) {
			return null;
		} else {
			throw new IllegalArgumentException("Unknown concept name type specified for " + header);
		}
	}
	
	/**
	 * @return a UUID for the given ConceptName TODO: Note, this is here temporarily, and is being added
	 *         in a separate ticket #141
	 */
	protected String generateConceptNameUuid(Object... args) {
		String seed = Arrays.stream(args).map(arg -> arg == null ? "null" : arg.toString()).collect(Collectors.joining("_"));
		String uuid = UUID.nameUUIDFromBytes(seed.getBytes()).toString();
		return uuid;
	}
}
