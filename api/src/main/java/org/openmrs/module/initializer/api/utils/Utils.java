package org.openmrs.module.initializer.api.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.SpecialityService;
import org.springframework.util.CollectionUtils;

public class Utils {
	
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
	 * @param specialityService
	 * @return The {@link Speciality} instance if found, null otherwise.
	 */
	public static Speciality fetchBahmniAppointmentSpeciality(String id, SpecialityService specialityService) {
		Speciality instance = null;
		if (instance == null) {
			instance = specialityService.getSpecialityByUuid(id);
		}
		if (instance == null) {
			for (Speciality currentSpeciality : specialityService.getAllSpecialities()) { //Because we don't have #specialityService.getSpecialityByName
				if (currentSpeciality.getName().equalsIgnoreCase(id)) {
					instance = currentSpeciality;
				}
			}
		}
		return instance;
	}
	
	/**
	 * Fetches Bahmni appointment service definition trying various routes.
	 * 
	 * @param id The appointment service definition name or UUID.
	 * @param appointmentServiceService
	 * @return The {@link AppointmentServiceDefinition} instance if found, null otherwise.
	 */
	public static AppointmentServiceDefinition fetchBahmniAppointmentServiceDefinition(String id,
	        AppointmentServiceDefinitionService appointmentServiceService) {
		AppointmentServiceDefinition instance = null;
		if (instance == null) {
			instance = appointmentServiceService.getAppointmentServiceByUuid(id);
		}
		if (instance == null) {
			for (AppointmentServiceDefinition currentAppointmentServiceDefinition : appointmentServiceService
			        .getAllAppointmentServices(false)) { //Because we don't have #appointmentServiceService.getAppointmentServiceDefinitionByName
				if (currentAppointmentServiceDefinition.getName().equalsIgnoreCase(id)) {
					instance = currentAppointmentServiceDefinition;
				}
			}
		}
		return instance;
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
}
