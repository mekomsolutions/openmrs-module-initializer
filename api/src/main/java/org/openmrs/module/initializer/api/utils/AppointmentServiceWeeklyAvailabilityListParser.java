package org.openmrs.module.initializer.api.utils;

import org.openmrs.module.appointments.model.ServiceWeeklyAvailability;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AppointmentServiceWeeklyAvailabilityListParser extends ListParser<ServiceWeeklyAvailability> {
	
	private AppointmentServiceDefinitionService appointmentServiceDefinitionService;
	
	@Autowired
	public AppointmentServiceWeeklyAvailabilityListParser(@Qualifier("appointmentServiceDefinitionService") AppointmentServiceDefinitionService appointmentServiceDefinitionService) {
		this.appointmentServiceDefinitionService = appointmentServiceDefinitionService;
	}
	
	@Override
	protected ServiceWeeklyAvailability fetch(String id) {
		return Utils.fetchAppointmentServiceWeeklyAvailability(id, appointmentServiceDefinitionService);
	}
	
}
