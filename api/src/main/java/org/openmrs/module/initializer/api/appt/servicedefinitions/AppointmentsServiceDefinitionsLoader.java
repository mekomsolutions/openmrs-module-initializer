package org.openmrs.module.initializer.api.appt.servicedefinitions;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsServiceDefinitionsLoader extends BaseCsvLoader<AppointmentServiceDefinition, AppointmentsServiceDefinitionsCsvParser> {
	
	@Autowired
	public void setParser(AppointmentsServiceDefinitionsCsvParser parser) {
		this.parser = parser;
	}
}
