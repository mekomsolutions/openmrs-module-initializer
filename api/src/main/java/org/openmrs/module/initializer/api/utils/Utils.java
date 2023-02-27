package org.openmrs.module.initializer.api.utils;

import com.github.freva.asciitable.AsciiTable;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.openmrs.module.initializer.api.BaseLineProcessor.LIST_SEPARATOR;

public class Utils {
	
	protected final static Logger log = LoggerFactory.getLogger(Utils.class);
	
	private static String[] setLineSeparators(String[] strings) {
		List<String> res = new ArrayList<>();
		for (String s : strings) {
			res.add(StringUtils.replace(s, LIST_SEPARATOR, LIST_SEPARATOR + System.lineSeparator()));
		}
		return res.toArray(new String[0]);
	}
	
	/**
	 * Prints as a pretty string a batch of CSV lines that share a common header.
	 * 
	 * @return The pretty string of CSV lines.
	 * @throws IllegalArgumentException as soon as one line has a diverging header from the others.
	 */
	public static String prettyPrint(List<CsvLine> lines) throws IllegalArgumentException {
		String[] prevHeader = null;
		
		List<String[]> data = new ArrayList<>();
		for (CsvLine line : lines) {
			if (prevHeader != null && !Arrays.equals(line.getHeaderLine(), prevHeader)) {
				throw new IllegalArgumentException(
				        "Printing a batch of CSV lines is only supported if they share a common header.");
			}
			prevHeader = line.getHeaderLine();
			data.add(setLineSeparators(line.asLine()));
		}
		return System.lineSeparator() + AsciiTable.getTable(prevHeader, data.toArray(new String[0][0]));
	}
	
	public static String pastePrint(List<CsvLine> lines) {
		StringBuilder sb = new StringBuilder();
		String[] prevHeader = null;
		for (CsvLine line : lines) {
			if (prevHeader != null && !Arrays.equals(line.getHeaderLine(), prevHeader)) {
				throw new IllegalArgumentException(
				        "Printing a batch of CSV lines is only supported if they share a common header.");
			}
			prevHeader = line.getHeaderLine();
			sb.append(StringUtils.join(line.asLine(), ",") + "\n");
		}
		sb.insert(0, StringUtils.join(prevHeader, ",") + "\n");
		return System.lineSeparator() + StringUtils.removeEnd(sb.toString(), "\n");
	}
	
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
			this(mappingStr, getSameAsConceptMapType(cs), cs);
		}
		
		public ConceptMap getConceptMapping() {
			ConceptReferenceTerm refTerm = cs.getConceptReferenceTermByCode(code, source);
			if (refTerm == null) {
				refTerm = new ConceptReferenceTerm(source, code, "");
			}
			return new ConceptMap(refTerm, mapType);
		}
		
		public boolean isSourceValid() {
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
	 * @param mapping The drug mapping, eg. "cambodia:123"
	 * @param service
	 * @return The {@link org.openmrs.Drug} instance that has a SAME-AS mapping to the given source nad
	 *         code, or null. This will return null if the reference source is not found, and the
	 *         SAME-AS concept map type is not found
	 */
	public static Drug getDrugByMapping(String mapping, ConceptService service) {
		Drug instance = null;
		if (StringUtils.isEmpty(mapping)) {
			return instance;
		}
		String[] parts = mapping.split(":");
		if (parts.length == 2) {
			ConceptSource source = fetchConceptSource(parts[0].trim(), service);
			if (source != null) {
				ConceptMapType sameAs = getSameAsConceptMapType(service);
				if (sameAs != null) {
					instance = service.getDrugByMapping(parts[1].trim(), source, Collections.singletonList(sameAs));
				}
			}
		}
		return instance;
	}
	
	/**
	 * @param conceptService the ConceptService
	 * @return the ConceptMapType that represents the SAME-AS mapping
	 */
	public static ConceptMapType getSameAsConceptMapType(ConceptService conceptService) {
		return conceptService.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
	}
	
	/**
	 * Fetches a ConceptSource, trying to match "id" on name, then hl7Code, then uniqueId, then uuid,
	 * 
	 * @param id the source lookup
	 * @param service the ConceptService
	 * @return The {@link ConceptSource} instance if found, null otherwise.
	 */
	public static ConceptSource fetchConceptSource(String id, ConceptService service) {
		ConceptSource instance = service.getConceptSourceByName(id);
		if (instance == null) {
			instance = service.getConceptSourceByHL7Code(id);
		}
		if (instance == null) {
			instance = service.getConceptSourceByUniqueId(id);
		}
		if (instance == null) {
			instance = service.getConceptSourceByUuid(id);
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
		Concept instance = service.getConceptByUuid(id);
		if (instance != null) {
			return instance;
		}
		instance = getConceptByMapping(id, service);
		if (instance != null) {
			return instance;
		}
		Locale originalLocale = Context.getLocale();
		try {
			for (Locale locale : LocaleUtility.getLocalesInOrder()) {
				Context.setLocale(locale);
				Concept concept = Context.getService(InitializerService.class).getConceptByName(id);
				if (concept != null) {
					if (!originalLocale.equals(locale)) {
						log.warn("Found '{}' in locale '{}', not in '{}'", new Object[] { id, locale, originalLocale });
					}
					return concept;
				}
			}
		}
		finally {
			Context.setLocale(originalLocale);
		}
		return null;
	}
	
	/**
	 * Fetches a ConceptMapType trying various routes for its "id".
	 * 
	 * @param id The ConceptMapType name or UUID.
	 * @param service
	 * @return The {@link ConceptMapType} instance if found, null otherwise.
	 */
	public static ConceptMapType fetchConceptMapType(String id, ConceptService service) {
		ConceptMapType instance = service.getConceptMapTypeByUuid(id);
		if (instance == null) {
			instance = service.getConceptMapTypeByName(id);
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
	 * Fetches a patient identifier type trying various routes for its "id".
	 * 
	 * @param id The patient identifier type name or UUID.
	 * @param service
	 * @return The {@link PatientIdentifierType} instance if found, null otherwise.
	 */
	public static PatientIdentifierType fetchPatientIdentifierType(String id, PatientService service) {
		PatientIdentifierType instance = null;
		if (instance == null) {
			instance = service.getPatientIdentifierTypeByUuid(id);
		}
		if (instance == null) {
			instance = service.getPatientIdentifierTypeByName(id);
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
	 * Fetches a program trying various routes for its "id".
	 * 
	 * @param id The program UUID, name or underlying concept identifier (name, UUID or 'same as'
	 *            concept mapping).
	 * @return The {@link Program} instance if found, null otherwise.
	 */
	public static Program fetchProgram(String id, ProgramWorkflowService pws, ConceptService cs) {
		Program instance = pws.getProgramByName(id);
		if (instance == null) {
			instance = pws.getProgramByUuid(id);
		}
		if (instance == null) {
			Concept c = Utils.fetchConcept(id, cs);
			if (c != null) {
				List<Program> progs = pws.getProgramsByConcept(c);
				if (!CollectionUtils.isEmpty(progs) && progs.size() == 1) {
					instance = progs.get(0);
				}
			}
		}
		return instance;
	}
	
	/**
	 * Fetches a program workflow trying various routes for its "id".
	 * 
	 * @param id The workflow UUID or underlying concept identifier (name, UUID or 'same as' concept
	 *            mapping).
	 * @return The {@link ProgramWorkflow} instance if found, null otherwise.
	 */
	public static ProgramWorkflow fetchProgramWorkflow(String id, ProgramWorkflowService pws, ConceptService cs) {
		ProgramWorkflow instance = pws.getWorkflowByUuid(id);
		if (instance == null) {
			Concept c = Utils.fetchConcept(id, cs);
			if (c != null) {
				List<ProgramWorkflow> workflows = pws.getProgramWorkflowsByConcept(c);
				if (!CollectionUtils.isEmpty(workflows) && workflows.size() == 1) {
					instance = workflows.get(0);
				}
			}
		}
		return instance;
	}
	
	/**
	 * Fetches a program workflow state trying various routes for its "id".
	 * 
	 * @param id The state UUID or underlying concept identifier (name, UUID or 'same as' concept
	 *            mapping).
	 * @return The {@link ProgramWorkflowState} instance if found, null otherwise.
	 */
	public static ProgramWorkflowState fetchProgramWorkflowState(String id, ProgramWorkflowService pws, ConceptService cs) {
		ProgramWorkflowState instance = pws.getStateByUuid(id);
		if (instance == null) {
			Concept c = Utils.fetchConcept(id, cs);
			if (c != null) {
				List<ProgramWorkflowState> states = pws.getProgramWorkflowStatesByConcept(c);
				if (!CollectionUtils.isEmpty(states) && states.size() == 1) {
					instance = states.get(0);
				}
			}
		}
		return instance;
	}
	
	/**
	 * Fetches a role trying various routes for its "id".
	 * 
	 * @param id The role UUID or name
	 * @return The {@link Role} instance if found, null otherwise.
	 */
	public static Role fetchRole(String id, UserService us) {
		Role instance = us.getRole(id);
		if (instance == null) {
			instance = us.getRoleByUuid(id);
		}
		return instance;
	}
	
	/**
	 * Fetches a privilege trying various routes for its "id".
	 * 
	 * @param id The privilege UUID or name
	 * @return The {@link Privilege} instance if found, null otherwise.
	 */
	public static Privilege fetchPrivilege(String id, UserService us) {
		Privilege instance = us.getPrivilege(id);
		if (instance == null) {
			instance = us.getPrivilegeByUuid(id);
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
	
	public static OrderType getParentOrderType(OrderService orderService, String javaClassName, String id) {
		OrderType parentOrdertype = null;
		if (javaClassName.equals("org.openmrs.Order")) {
			parentOrdertype = fetchOrderType(orderService, id);
		}
		// TODO verify if Context.getOrderService() is enough to handle more specific java class names (...like org.openmrs.DrugOrder)
		return parentOrdertype;
	}
	
	/**
	 * Fetches an order type trying various routes.
	 * 
	 * @param id The order type name or UUID.
	 * @param orderService
	 * @return The {@link OrderType} instance if found, null otherwise.
	 */
	public static OrderType fetchOrderType(OrderService orderService, String id) {
		OrderType instance = null;
		if (instance == null) {
			id = UUID.fromString(id).toString();
			instance = orderService.getOrderTypeByUuid(id);
		}
		if (instance == null) {
			instance = orderService.getOrderTypeByName(id);
		}
		return instance;
	}
	
	/**
	 * Fetches a ConceptClass trying various routes for its "id".
	 * 
	 * @param id The ConceptClass UUID or name
	 * @return The {@link ConceptClass} instance if found, null otherwise.
	 */
	public static ConceptClass fetchConceptClass(String id, ConceptService cs) {
		ConceptClass instance = cs.getConceptClassByName(id);
		if (instance == null) {
			instance = cs.getConceptClassByUuid(id);
		}
		return instance;
	}
	
	/**
	 * Fetches Bahmni appointment speciality trying various routes.
	 * 
	 * @param id The appointment speciality name or UUID.
	 * @param service The {@link SpecialityService}.
	 * @return The {@link Speciality} instance if found, null otherwise.
	 */
	public static Speciality fetchBahmniAppointmentSpeciality(String id, SpecialityService service) {
		Speciality instance = service.getSpecialityByUuid(id);
		if (instance == null) {
			instance = service.getAllSpecialities().stream().filter(s -> s.getName().equalsIgnoreCase(id)).findAny()
			        .orElse(null);
		}
		return instance;
	}
	
	/**
	 * Fetches Bahmni appointment service definition trying various routes.
	 * 
	 * @param id The appointment service definition name or UUID.
	 * @param service The {@link AppointmentServiceDefinitionService}.
	 * @return The {@link AppointmentServiceDefinition} instance if found, null otherwise.
	 */
	public static AppointmentServiceDefinition fetchBahmniAppointmentServiceDefinition(String id,
	        AppointmentServiceDefinitionService service) {
		AppointmentServiceDefinition def = service.getAppointmentServiceByUuid(id);
		if (def == null) {
			def = service.getAllAppointmentServices(false).stream().filter(d -> d.getName().equalsIgnoreCase(id)).findAny()
			        .orElse(null);
		}
		return def;
	}
	
	/**
	 * Fetches Bahmni appointment service types trying various routes.
	 * 
	 * @param id The appointment service type name or UUID.
	 * @param service The {@link AppointmentServiceDefinitionService}.
	 * @return The {@link AppointmentServiceType} instance if found, null otherwise.
	 * @since 2.1.0
	 */
	public static AppointmentServiceType fetchBahmniAppointmentServiceType(String id,
	        AppointmentServiceDefinitionService service) {
		AppointmentServiceType type = service.getAppointmentServiceTypeByUuid(id);
		if (type == null) {
			for (AppointmentServiceDefinition def : service.getAllAppointmentServices(false)) {
				type = def.getServiceTypes().stream().filter(t -> t.getName().equalsIgnoreCase(id)).findAny().orElse(null);
				if (type != null) {
					break;
				}
			}
		}
		return type;
	}
	
	/**
	 * Checks if a location is an appointment location
	 * 
	 * @param location The location to check
	 * @return true if the location is an appointment location, false otherwise.
	 */
	public static boolean isAppointmentLocation(Location location) {
		for (LocationTag tag : location.getTags()) {
			if (tag.getName().equalsIgnoreCase("Appointment Location")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param property the system property or runtime property to lookup
	 * @return the system property value if a system property with the passed property name exists, the
	 *         runtime property value otherwise
	 */
	public static String getPropertyValue(String property) {
		return getPropertyValue(property, null);
	}
	
	/**
	 * @param property the system property or runtime property to lookup
	 * @param defaultValue the default value to return if the property is not set
	 * @return the system property value if a system property with the passed property name exists, the
	 *         runtime property value otherwise
	 */
	public static String getPropertyValue(String property, String defaultValue) {
		if (System.getProperties().containsKey(property)) {
			return System.getProperty(property);
		}
		return Context.getRuntimeProperties().getProperty(property, defaultValue);
	}
	
	/**
	 * Concatenates the {@link Object#toString()} representation of each object of an array or "null"
	 * string if object is null, and use it as a seed to generate a UUID.
	 * 
	 * @param args An array of {@link Object}.
	 * @return The generated UUID.
	 */
	public static String generateUuidFromObjects(Object... args) {
		String seed = Arrays.stream(args).map(arg -> arg == null ? "null" : arg.toString()).collect(Collectors.joining("_"));
		String uuid = UUID.nameUUIDFromBytes(seed.getBytes()).toString();
		return uuid;
	}
	
	/*
	 * Turns a proxy short class name into the original short class name.
	 * Eg. "EncounterType$HibernateProxy$ODcBnusu" or "EncounterType_$$_javassist_26" → "EncounterType"
	 */
	public static String unProxy(String shortClassName) {
		shortClassName = StringUtils.substringBefore(shortClassName, "_$");
		shortClassName = StringUtils.substringBefore(shortClassName, "$");
		return shortClassName;
	}
}
