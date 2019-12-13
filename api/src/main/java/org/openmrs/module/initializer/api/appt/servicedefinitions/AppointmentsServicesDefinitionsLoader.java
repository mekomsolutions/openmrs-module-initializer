package org.openmrs.module.initializer.api.appt.servicedefinitions;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsServicesDefinitionsLoader extends BaseCsvLoader<AppointmentServiceDefinition, AppointmentsServicesDefinitionsCsvParser> {
	
	@Autowired
	public void setParser(AppointmentsServicesDefinitionsCsvParser parser) {
		this.parser = parser;
	}
}
