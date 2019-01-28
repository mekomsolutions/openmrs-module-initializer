package org.openmrs.module.initializer.api.patients;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.persons.PersonLineProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientLineProcessor extends BaseLineProcessor<Patient, PatientService> {
	
	public static final String HEADER_PATIENT_IDENTIFIERS = "Identifiers";

	/* Date Created is available for Patients but not Persons because OpenMRS
		always overwrites createdDate for Persons when saving
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
		
		return pt;
	}
	
	@Override
	protected Patient fill(Patient pt, CsvLine line) throws IllegalArgumentException {
		String[] personLine = this.headerLine.clone();
		PersonLineProcessor personLineProcessor = new PersonLineProcessor(personLine, null);
		pt = (Patient) personLineProcessor.fill(pt, line);
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

		String createdDateString = line.get(HEADER_PERSON_DATE_CREATED);
		if (createdDateString != null && !createdDateString.trim().isEmpty()) {
			SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			try {
				Date personDateCreated = ISO8601.parse(createdDateString);
				pt.setDateCreated(personDateCreated);
				pt.setPersonDateCreated(personDateCreated);
			}
			catch (ParseException err) {
				log.warn("Failed to parse Date Created. Should be formatted like ISO 8601 with no colon in the timezone, "
						+ "e.g. 2016-01-01T00:00:00+0100. On line\n" + line);
			}
		} else if (pt.getDateCreated() == null) {
			pt.setDateCreated(new Date());
		} // if there's no dateCreated provided by the CSV and the pt already has one, do
		// nothing


		return pt;
	}
}
