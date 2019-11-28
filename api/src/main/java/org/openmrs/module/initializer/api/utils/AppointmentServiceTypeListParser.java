package org.openmrs.module.initializer.api.utils;

import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AppointmentServiceTypeListParser extends ListParser<AppointmentServiceType> {
	
	private AppointmentServiceDefinitionService appointmentServiceDefinitionService;
	
	@Autowired
	public AppointmentServiceTypeListParser(@Qualifier("appointmentServiceDefinitionService") AppointmentServiceDefinitionService appointmentServiceDefinitionService) {
		this.appointmentServiceDefinitionService = appointmentServiceDefinitionService;
	}
	
	@Override
	protected AppointmentServiceType fetch(String id) {
		return Utils.fetchAppointmentServiceType(id, appointmentServiceDefinitionService);
	}
	
}
