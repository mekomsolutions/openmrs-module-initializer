package org.openmrs.module.initializer.api.appt.specialities;

import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialitiesLoader extends BaseCsvLoader<Speciality, SpecialitiesCsvParser> {
	
	@Autowired
	public void setParser(SpecialitiesCsvParser parser) {
		this.parser = parser;
	}
}
