package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.BooleanUtils;
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
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
	
	final public static String HEADER_VERSION = "version";

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
		
		// Load in all concept names from the CSV Line
		List<ConceptName> namesFromCsv = new ArrayList<>();
		for (String conceptNameHeader : getConceptNameHeaders(line)) {
			ConceptName conceptName = constructConceptName(concept, line, conceptNameHeader);
			if (conceptName != null) {
				namesFromCsv.add(conceptName);
			}
		}
		
		// Configure Concept Names with defaults
		ensureLocalPreferredConfigured(namesFromCsv);

		// Update any existing Concept Names as appropriate
		for (ConceptName existingName : concept.getNames(true)) {

			List<ConceptName> matchingNames = new ArrayList<>();

			// Find a matching concept name by uuid
			for (ConceptName nameFromCsv : namesFromCsv) {
				if (existingName.getUuid().equals(nameFromCsv.getUuid())) {
					matchingNames.add(nameFromCsv);
				}
			}
			// If no match is found on uuid, match if an existing concept and new concept would share a generated uuid
			if (matchingNames.isEmpty()) {
				String existingGeneratedUuid = generateConceptNameUuid(concept, existingName);
				for (ConceptName nameFromCsv : namesFromCsv) {
					if (StringUtils.isEmpty(nameFromCsv.getUuid())) {
						String csvGeneratedUuid = generateConceptNameUuid(concept, nameFromCsv);
						if (existingGeneratedUuid.equalsIgnoreCase(csvGeneratedUuid)) {
							matchingNames.add(nameFromCsv);
							nameFromCsv.setUuid(csvGeneratedUuid);
						}
					}
				}
			}

			// If there are multiple matches on uuid, fail loading this row
			if (matchingNames.size() > 1) {
				StringBuilder msg = new StringBuilder();
				msg.append("There are ").append(matchingNames.size()).append(" names defined in the CSV ");
				msg.append("that match the an existing Concept Name: ").append(existingName);
				msg.append("Names from CSV: " + matchingNames);
				throw new IllegalArgumentException(msg.toString());
			}
			// If there is a single match on uuid, update the existing Concept
			else if (matchingNames.size() == 1) {
				ConceptName newName = matchingNames.get(0);

				// Validate and fail if the name has changed from the existing value
				// This is necessary because if the name matches and the uuid does not, the ConceptServiceImpl
				// will automatically void the name, clone it, and issue a new random UUID to the new one, which
				// will result in a state that differs from what was specified in the concepts CSV

				if (!existingName.getName().equals(newName.getName())) {
					StringBuilder msg = new StringBuilder();
					msg.append("It is not permitted to change the name property of an existing ConceptName and ");
					msg.append("retain the same uuid as the previous name.  Users who wish to explicitly set their ");
					msg.append("ConceptName uuids should assign a new UUID whenever they change the name, ");
					msg.append("and either void the previous name or change the Concept Name Type to a Synonym. ");
					msg.append("Any name removed from the CSV will result in this name being voided.");
					throw new IllegalArgumentException(msg.toString());
				}

				existingName.setConceptNameType(newName.getConceptNameType());
				existingName.setName(newName.getName());
				existingName.setLocale(newName.getLocale());
				existingName.setLocalePreferred(newName.getLocalePreferred());

				// If the matched name was previously voided, un-void it
				if (BooleanUtils.isTrue(existingName.getVoided())) {
					existingName.setVoided(false);
					existingName.setDateVoided(null);
					existingName.setVoidedBy(null);
					existingName.setVoidReason(null);
				}
				namesFromCsv.remove(newName);
			}
			// If there is no match found, then void the existing name, as there may be data associated with it
			else {
				existingName.setVoided(true);
				existingName.setDateVoided(new Date());
				existingName.setVoidedBy(Context.getAuthenticatedUser());
				existingName.setVoidReason(InitializerConstants.DEFAULT_VOID_REASON);
			}
		}

		// Now, add in any new Concept Names
		for (ConceptName newName : namesFromCsv) {
			if (StringUtils.isEmpty(newName.getUuid())) {
				newName.setUuid(generateConceptNameUuid(concept, newName));
			}
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
		
		// Concept version
		if (line.containsHeader(HEADER_VERSION)) {
			String conceptVersion = line.getString(HEADER_VERSION, null);
			concept.setVersion(conceptVersion);
		}

		return concept;
	}
	
	/**
	 * Given a CsvLine, this method returns all of the headers that refer to actual ConceptName.name
	 * values These are used both to retrieve the text for each ConceptName directly, and as a prefix
	 * for related attributes.
	 */
	protected List<String> getConceptNameHeaders(CsvLine line) {
		List<String> headers = new ArrayList<>();
		for (String header : line.getHeaderLine()) {
			String[] headerComponents = header.split(LOCALE_SEPARATOR, 3);
			String h = headerComponents[0].trim().toLowerCase();
			if (h.startsWith(HEADER_FSNAME) || h.startsWith(HEADER_SHORTNAME) || h.startsWith(HEADER_SYNONYM)) {
				if (headerComponents.length == 1) {
					throw new IllegalArgumentException("Concept Name Headers must specify a locale as <name>:<locale>");
				} else if (headerComponents.length == 2) {
					headers.add(header);
				}
			}
		}
		return headers;
	}

	/**
	 * @return a new ConceptName instance for the given nameHeader If the passed nameHeader is "fully
	 *         specified name:en", this will construct the concept name from fully specified name:en +
	 *         fully specified name:en:preferred + fully specified name:en:uuid The preferred and uuid
	 *         values are optional, if not supplied then preferred will default to false, and the uuid
	 *         will be generated in a deterministic fashion based on name, locale, and type, which
	 *         enables future updates to concept names to match, even if not explicit uuid is supplied
	 */
	protected ConceptName constructConceptName(Concept concept, CsvLine line, String nameHeader) {
		ConceptName cn = null;
		String name = line.get(nameHeader);
		if (StringUtils.isNotEmpty(name)) {
			Locale locale = LocaleUtils.toLocale(nameHeader.split(LOCALE_SEPARATOR)[1]);
			cn = new ConceptName(name, locale);

			ConceptNameType nameType = getConceptNameTypeForHeader(nameHeader);
			cn.setConceptNameType(nameType);
			Boolean localePreferred = line.getBool(nameHeader + LOCALE_SEPARATOR + HEADER_PREFERRED);
			localePreferred = (localePreferred == null ? Boolean.FALSE : localePreferred);
			cn.setLocalePreferred(localePreferred);

			String uuid = line.get(nameHeader + LOCALE_SEPARATOR + HEADER_UUID);
			cn.setUuid(uuid);
		}
		return cn;
	}

	/**
	 * Iterates over all specified concept names and ensures that locale preferred is set on one concept
	 * per locale
	 */
	protected void ensureLocalPreferredConfigured(List<ConceptName> conceptNames) {
		Map<Locale, ConceptName> localePreferredNames = new HashMap<>();
		for (ConceptName conceptName : conceptNames) {
			if (BooleanUtils.isTrue(conceptName.getLocalePreferred())) {
				if (localePreferredNames.get(conceptName.getLocale()) != null) {
					throw new IllegalArgumentException("Only one name in a locale can be marked as preferred");
				}
				localePreferredNames.put(conceptName.getLocale(), conceptName);
			} else {
				conceptName.setLocalePreferred(false);
			}
		}
		for (ConceptName conceptName : conceptNames) {
			if (localePreferredNames.get(conceptName.getLocale()) == null) {
				ConceptNameType nameType = conceptName.getConceptNameType();
				// Only synonyms and fully specified names are allowed to be preferred
				if (nameType == null || nameType == ConceptNameType.FULLY_SPECIFIED) {
					conceptName.setLocalePreferred(true);
					localePreferredNames.put(conceptName.getLocale(), conceptName);
				}
			}
		}
	}

	/**
	 * @return the ConceptNameType represented by a given ConceptName component header, or null if
	 *         header does not represent a component of a ConceptName
	 */
	protected ConceptNameType getConceptNameTypeForHeader(String header) {
		header = header.toLowerCase();
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
	 * @return a UUID for the given ConceptName
	 */
	protected String generateConceptNameUuid(Concept concept, ConceptName cn) {
		String name = cn.getName();
		ConceptNameType type = cn.getConceptNameType();
		Locale locale = cn.getLocale();
		return Utils.generateUuidFromObjects(concept.getUuid(), name, type, locale);
	}
}
