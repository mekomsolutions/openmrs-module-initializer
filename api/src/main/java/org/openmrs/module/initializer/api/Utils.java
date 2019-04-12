package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;

public class Utils {
	
	private static final Log log = LogFactory.getLog(Utils.class);
	
	/**
	 * Helps build a {@link ConceptMap} out the usual string inputs.
	 */
	public static class ConceptMappingWrapper {
		
		private ConceptService cs;
		
		private ConceptSource source;
		
		private String code;
		
		private ConceptMapType mapType;
		
		public ConceptMappingWrapper(String mappingStr, ConceptMapType mapType, ConceptService cs) {
			this.cs = cs;
			String[] parts = mappingStr.split(":");
			if (parts.length == 2) {
				source = cs.getConceptSourceByName(parts[0].trim());
				code = parts[1].trim();
			}
			this.mapType = mapType;
		}
		
		public ConceptMappingWrapper(String mappingStr, ConceptService cs) {
			this(mappingStr, cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID), cs);
		}
		
		public ConceptMap getConceptMapping() {
			ConceptReferenceTerm refTerm = cs.getConceptReferenceTermByCode(code, source);
			if (refTerm == null) {
				refTerm = new ConceptReferenceTerm(source, code, "");
			}
			return new ConceptMap(refTerm, mapType);
		}
		
		public boolean isValid() {
			return source != null;
		}
	}
	
	/**
	 * @param mapping The concept mapping, eg. "cambodia:123"
	 * @param service
	 * @return The {@link Concept} instance if found, null otherwise.
	 */
	public static Concept getConceptByMapping(String mapping, ConceptService service) {
		Concept instance = null;
		if (StringUtils.isEmpty(mapping)) {
			return instance;
		}
		String[] parts = mapping.split(":");
		if (parts.length == 2) {
			instance = service.getConceptByMapping(parts[1].trim(), parts[0].trim());
		}
		return instance;
	}
	
	/**
	 * Fetches a concept trying various routes for its "id".
	 * 
	 * @param id The concept mapping ("cambodia:123"), concept name or concept UUID.
	 * @param service
	 * @return The {@link Concept} instance if found, null otherwise.
	 */
	public static Concept fetchConcept(String id, ConceptService service) {
		Concept instance = null;
		if (instance == null) {
			instance = service.getConceptByUuid(id);
		}
		if (instance == null) {
			instance = service.getConceptByName(id);
		}
		if (instance == null) {
			instance = getConceptByMapping(id, service);
		}
		return instance;
	}
	
	/**
	 * Guesses a 'best match' name for a given concept.
	 * 
	 * @param concept Concept Object.
	 * @param locale
	 * @return conceptName string if found, null otherwise.
	 */
	public static String getBestMatchName(Concept concept, Locale locale) {
		ConceptName conceptName = null;
		if (concept.getPreferredName(locale) != null) {
			conceptName = concept.getPreferredName(locale);
		} else if (concept.getFullySpecifiedName(locale) != null) {
			conceptName = concept.getFullySpecifiedName(locale);
		} else if (concept.getName(locale, true) != null) {
			conceptName = concept.getName(locale, true);
		} else if (concept.getName(locale) != null) {
			conceptName = concept.getName(locale);
		} else {
			conceptName = concept.getName();
		}
		return conceptName.getName();
	}
	
	/**
	 * Guesses a 'best match' description for a given concept.
	 * 
	 * @param concept Concept Object.
	 * @param locale
	 * @return conceptDescription string if found, null otherwise.
	 */
	public static String getBestMatchDescription(Concept concept, Locale locale) {
		String conceptDescription = null;
		if (concept.getDescription(locale, true) != null) {
			conceptDescription = concept.getDescription(locale, true).getDescription();
		} else if (concept.getDescription(locale) != null) {
			conceptDescription = concept.getDescription(locale).getDescription();
		} else {
			conceptDescription = getBestMatchName(concept, locale);
		}
		return conceptDescription;
	}
	
	/**
	 * Fetches a person attribute type trying various routes for its "id".
	 * 
	 * @param id The person attribute type name or UUID.
	 * @param service
	 * @return The {@link PersonAttributeType} instance if found, null otherwise.
	 */
	public static PersonAttributeType fetchPersonAttributeType(String id, PersonService service) {
		PersonAttributeType instance = null;
		if (instance == null) {
			instance = service.getPersonAttributeTypeByUuid(id);
		}
		if (instance == null) {
			instance = service.getPersonAttributeTypeByName(id);
		}
		return instance;
	}
	
	/**
	 * Fetches a location trying various routes for its "id".
	 * 
	 * @param id The location name or UUID.
	 * @param service
	 * @return The {@link Location} instance if found, null otherwise.
	 */
	public static Location fetchLocation(String id, LocationService service) {
		Location instance = null;
		if (instance == null) {
			instance = service.getLocationByUuid(id);
		}
		if (instance == null) {
			instance = service.getLocation(id);
		}
		return instance;
	}
	
	/**
	 * Fetches a location tag trying various routes for its "id".
	 * 
	 * @param id The location tag name or UUID.
	 * @param service
	 * @return The {@link LocationTag} instance if found, null otherwise.
	 */
	public static LocationTag fetchLocationTag(String id, LocationService service) {
		LocationTag instance = null;
		if (instance == null) {
			instance = service.getLocationTagByUuid(id);
		}
		if (instance == null) {
			instance = service.getLocationTagByName(id);
		}
		return instance;
	}
	
	/**
	 * Convenience method to serialize a JSON object that also handles the simple string case.
	 */
	public static String asString(Object jsonObj) throws JsonGenerationException, JsonMappingException, IOException {
		if (jsonObj == null) {
			return "";
		}
		if (jsonObj instanceof String) {
			return (String) jsonObj;
		} else {
			return (new ObjectMapper()).writeValueAsString(jsonObj);
		}
	}
	
	/**
	 * Convenience method to read a list of string out of a JSON string.
	 */
	public static List<String> asStringList(String jsonString) throws JsonParseException, JsonMappingException, IOException {
		List<Object> list = (new ObjectMapper()).readValue(jsonString, List.class);
		
		List<String> stringList = new ArrayList<String>();
		for (Object o : list) {
			stringList.add(asString(o));
		}
		return stringList;
	}
}
