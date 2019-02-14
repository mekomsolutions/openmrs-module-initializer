package org.openmrs.module.initializer.api.patients;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.persons.PersonLineProcessor;

import java.util.Date;

public class PatientLineProcessor extends BaseLineProcessor<Patient, PatientService> {
	
	public static final String HEADER_PATIENT_IDENTIFIERS = "Identifiers";
	
	/*
	 * Date Created is available for Patients but not Persons because OpenMRS always
	 * overwrites createdDate for Persons when saving
	 */
	public static final String HEADER_PERSON_DATE_CREATED = "Date created";
	
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
		pt.setPersonVoided(getVoidOrRetire(line.asLine()));
		pt.setPersonVoidReason("Voided by module " + InitializerConstants.MODULE_NAME);
		
		return pt;
	}
	
	@Override
	protected Patient fill(Patient pt, CsvLine line) throws IllegalArgumentException {
		String[] personLine = this.headerLine.clone();
		PersonLineProcessor personLineProcessor = new PersonLineProcessor(personLine, null);
		pt = (Patient) personLineProcessor.fill(pt, line);
		LocationService locationService = Context.getService(LocationService.class);
		String pisString = line.get(HEADER_PATIENT_IDENTIFIERS, true);
		String[] pisArray = pisString.split(";");
		for (int i = 0; i < pisArray.length; i++) {
			String piString = pisArray[i];
			String[] piParts = piString.split(":");
			if (piParts.length < 3) {
				throw new IllegalArgumentException(String.format("Ignoring invalid patient identifier entry '%s'."
				        + "Patient identifiers should be formatted like 'id_name:id:id_location;...'.",
				    piString));
			}
			PatientIdentifierType pit = service.getPatientIdentifierTypeByName(piParts[0]);
			Location piLocation = locationService.getLocation(piParts[2]);
			PatientIdentifier pi = new PatientIdentifier(piParts[1], pit, piLocation);
			if (i == 0) {
				pi.setPreferred(true);
			}
			pt.addIdentifier(pi);
		}
		
		String createdDateString = line.get(HEADER_PERSON_DATE_CREATED);
		if (createdDateString != null && !createdDateString.trim().isEmpty()) {
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
			Date date = parser.parseDateTime(createdDateString).toDate();
			pt.setDateCreated(date);
			pt.setPersonDateCreated(date);
		} else if (pt.getDateCreated() == null) {
			pt.setDateCreated(new Date());
		} // if there's no dateCreated provided by the CSV and the pt already has one, do
		  // nothing
		
		return pt;
	}
}
