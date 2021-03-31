package org.openmrs.module.initializer.api.appt.servicetypes;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentServiceTypesLoader extends BaseCsvLoader<AppointmentServiceType, AppointmentServiceTypesCsvParser> {
	
	@Autowired
	public void setParser(AppointmentServiceTypesCsvParser parser) {
		this.parser = parser;
	}
}
