package org.openmrs.module.initializer.api.appt.specialities;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsSpecialitiesLoader extends BaseCsvLoader<Speciality, SpecialitiesCsvParser> {
	
	@Autowired
	public void setParser(SpecialitiesCsvParser parser) {
		this.parser = parser;
	}
}
