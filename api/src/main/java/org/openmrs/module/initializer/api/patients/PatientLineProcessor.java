package org.openmrs.module.initializer.api.patients;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class PatientLineProcessor extends BaseLineProcessor<Patient, PatientService> {
	
	public static final String HEADER_PATIENT_IDENTIFIERS = "Identifiers";
	
	public static final String HEADER_NAME_GIVEN = "Given names";
	
	public static final String HEADER_NAME_MIDDLE = "Middle names";
	
	public static final String HEADER_NAME_FAMILY = "Family names";
	
	public static final String HEADER_GENDER = "Gender";
	
	public static final String HEADER_BIRTHDATE = "Birthdate";
	
	public static final String HEADER_PERSON_DATE_CREATED = "Date created";
	
	public static final String HEADER_ADDRESSES = "Addresses";
	
	public PatientLineProcessor(String[] headerLine, PatientService ps) {
		super(headerLine, ps);
	}
	
	@Override
	protected Patient bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Patient pt = service.getPatientByUuid(uuid);
		
		if (pt == null) {
			pt = new Patient();
			if (!StringUtils.isEmpty(uuid)) {
				pt.setUuid(uuid);
			}
		}
		
		pt.setVoided(getVoidOrRetire(line.asLine()));
		
		return pt;
	}
	
	@Override
	protected Patient fill(Patient pt, CsvLine line) throws IllegalArgumentException {
		log.debug("Starting " + line);
		LocationService locationService = Context.getService(LocationService.class);
		String pisString = line.get(HEADER_PATIENT_IDENTIFIERS);
		String[] pisArray = pisString.split(";");
		for (int i = 0; i < pisArray.length; i++) {
			String piString = pisArray[i];
			String[] piParts = piString.split(":");
			if (piParts.length < 3) {
				log.warn(
				    String.format("Ignoring invalid patient identifier entry '%s'. Patient identifiers should be formatted "
				            + "like 'id_name:id:id_location;...'. On line %s",
				        piString, line));
				continue;
			}
			PatientIdentifierType pit = service.getPatientIdentifierTypeByName(piParts[0]);
			Location piLocation = locationService.getLocation(piParts[2]);
			PatientIdentifier pi = new PatientIdentifier(piParts[1], pit, piLocation);
			if (i == 0) {
				pi.setPreferred(true);
			}
			pt.addIdentifier(pi);
		}
		
		{
			String[] nameLists = { line.get(HEADER_NAME_GIVEN), line.get(HEADER_NAME_MIDDLE), line.get(HEADER_NAME_FAMILY) };
			String[][] names = new String[3][]; // String [name field] [name index]
			List<Integer> lengths = new ArrayList<Integer>();
			for (int i = 0; i < nameLists.length; i++) {
				log.warn("nameLists");
				log.warn(nameLists);
				if (nameLists[i] != null) {
					names[i] = nameLists[i].split(",");
					lengths.add(names[i].length);
				}
			}
			int length;
			if (new HashSet<Integer>(lengths).size() > 1) {
				length = Collections.min(lengths);
				log.warn("Name array length mismatch. Please make sure that all non-empty name fields have the same "
				        + "number of comma-delimited elements. Padding the end of the shorter arrays with null. "
				        + "On line\n" + line);
			} else {
				length = lengths.get(0);
			}
			
			for (int i = 0; i < length; i++) {
				PersonName name = new PersonName(safeGet(names, 0, i), safeGet(names, 1, i), safeGet(names, 2, i));
				if (i == 0) {
					name.setPreferred(true);
				}
				pt.addName(name);
			}
		}
		
		pt.setGender(line.get(HEADER_GENDER));
		
		String birthdateString = line.get(HEADER_BIRTHDATE);
		if (birthdateString != null && !birthdateString.trim().isEmpty()) {
			DateFormat birthdateFormat = new SimpleDateFormat("y-M-d");
			try {
				Date birthdate = birthdateFormat.parse(birthdateString);
				pt.setBirthdate(birthdate);
			}
			catch (ParseException err) {
				log.warn("Failed to parse Birthdate. Should be formatted y-m-d. On line\n" + line);
			}
		}
		
		String createdDateString = line.get(HEADER_PERSON_DATE_CREATED);
		if (createdDateString != null && !createdDateString.trim().isEmpty()) {
			SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			try {
				Date personDateCreated = ISO8601.parse(createdDateString);
				pt.setDateCreated(personDateCreated);
			}
			catch (ParseException err) {
				log.warn("Failed to parse Date Created. Should be formatted like ISO 8601 with no colon in the timezone, "
				        + "e.g. 2016-01-01T00:00:00+0100. On line\n" + line);
			}
		} else if (pt.getDateCreated() == null) {
			pt.setDateCreated(new Date());
		} // if there's no dateCreated provided by the CSV and the pt already has one, do
		  // nothing
		
		String addressesString = line.get(HEADER_ADDRESSES);
		if (addressesString != null && !addressesString.trim().isEmpty()) {
			String[] addressesArray = addressesString.split(";");
			for (String addressString : addressesArray) {
				PersonAddress address = new PersonAddress();
				String[] addressFields = addressString.split(",");
				for (String addressFieldString : addressFields) {
					String[] addressFieldParts = addressFieldString.split(":");
					setAddressField(address, addressFieldParts[0], addressFieldParts[1], line.toString());
				}
				pt.addAddress(address);
			}
		}
		
		log.debug("Finished " + line);
		return pt;
	}
	
	private static void setAddressField(PersonAddress target, String addressField, String value, String line) {
		String setMethodName = "set" + addressField.substring(0, 1).toUpperCase() + addressField.substring(1);
		try {
			Method method = target.getClass().getMethod(setMethodName, String.class);
			try {
				method.invoke(target, value);
			}
			catch (IllegalArgumentException e) {
				log.warn(String.format("Bad value %s for address field %s for line %s\n%s", value, addressField, line, e));
			}
			catch (IllegalAccessException e) {
				log.warn(String.format("Unable to set address field %s for line %s\n%s", addressField, line, e));
			}
			catch (InvocationTargetException e) {
				log.warn(String.format("Unable to set address field %s for line %s\n%s", addressField, line, e));
			}
		}
		catch (SecurityException e) {
			log.warn(String.format("Unable to set address field %s for line %s\n%s", addressField, line, e));
		}
		catch (NoSuchMethodException e) {
			log.warn(String.format("Invalid address field %s in line %s", addressField, line));
		}
		
	}
	
	private static <T> T safeGet(T[][] array, int index1, int index2) {
		if (array != null && array.length > index1 && array[index1] != null && array[index1].length > index2) {
			return array[index1][index2];
		} else {
			return null;
		}
	}
	
	private static <T> T safeGet(T[] array, int index) {
		if (array != null && array.length > index) {
			return array[index];
		} else {
			return null;
		}
	}
}
