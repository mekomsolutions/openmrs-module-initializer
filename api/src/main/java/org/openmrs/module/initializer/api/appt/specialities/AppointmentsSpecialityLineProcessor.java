package org.openmrs.module.initializer.api.appt.specialities;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

/**
 * This is the first level line processor for specialities. It allows to parse and save specialities
 * with the minimal set of required fields.
 */
@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsSpecialityLineProcessor extends BaseLineProcessor<Speciality> {
	
	public Speciality fill(Speciality speciality, CsvLine line) throws IllegalArgumentException {
		speciality.setName(line.getName(true));
		return speciality;
	}
}
