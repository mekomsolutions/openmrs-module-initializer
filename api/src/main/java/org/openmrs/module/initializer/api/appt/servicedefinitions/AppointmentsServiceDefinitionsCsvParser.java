package org.openmrs.module.initializer.api.appt.servicedefinitions;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsServiceDefinitionsCsvParser extends CsvParser<AppointmentServiceDefinition, BaseLineProcessor<AppointmentServiceDefinition>> {
	
	private AppointmentServiceDefinitionService appointmentServiceService;
	
	@Autowired
	public AppointmentsServiceDefinitionsCsvParser(@Qualifier("appointmentServiceService") AppointmentServiceDefinitionService appointmentServiceService,
	    AppointmentsServiceDefinitionLineProcessor processor) {
		super(processor);
		this.appointmentServiceService = appointmentServiceService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.APPOINTMENTS_SERVICE_DEFINITIONS;
	}
	
	@Override
	protected AppointmentServiceDefinition save(AppointmentServiceDefinition instance) {
		return appointmentServiceService.save(instance);
	}
}
