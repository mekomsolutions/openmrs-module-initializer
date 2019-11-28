package org.openmrs.module.initializer.api.appt.appointmentservicedefinitions;

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
	
	private AppointmentServiceDefinitionService appointmentServiceDefinitionService;
	
	@Autowired
	public AppointmentsServiceDefinitionsCsvParser(@Qualifier("appointmentServiceDefinitionService") AppointmentServiceDefinitionService appointmentServiceDefinitionService,
	    AppointmentsServiceDefinitionLineProcessor processor) {
		super(processor);
		this.appointmentServiceDefinitionService = appointmentServiceDefinitionService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.APPOINTMENTS_SPECIALITIES;
	}
	
	@Override
	protected AppointmentServiceDefinition save(AppointmentServiceDefinition instance) {
		return appointmentServiceDefinitionService.save(instance);
	}
}
