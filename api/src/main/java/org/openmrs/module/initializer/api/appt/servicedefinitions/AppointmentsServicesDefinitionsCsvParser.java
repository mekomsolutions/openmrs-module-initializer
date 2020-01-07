package org.openmrs.module.initializer.api.appt.servicedefinitions;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsServicesDefinitionsCsvParser extends CsvParser<AppointmentServiceDefinition, BaseLineProcessor<AppointmentServiceDefinition>> {
	
	private AppointmentServiceDefinitionService appointmentServiceService;
	
	@Autowired
	public AppointmentsServicesDefinitionsCsvParser(
	    @Qualifier("appointmentServiceService") AppointmentServiceDefinitionService appointmentServiceService,
	    AppointmentsServiceDefinitionLineProcessor processor) {
		super(processor);
		this.appointmentServiceService = appointmentServiceService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.APPOINTMENTS_SERVICES_DEFINITIONS;
	}
	
	@Override
	public AppointmentServiceDefinition bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		AppointmentServiceDefinition definition = appointmentServiceService.getAppointmentServiceByUuid(uuid);
		
		if (definition == null) {
			String name = line.getName(true); // should fail if name column missing
			definition = Utils.fetchBahmniAppointmentServiceDefinition(name, appointmentServiceService);
		}
		
		if (definition == null) {
			definition = new AppointmentServiceDefinition();
			if (!StringUtils.isEmpty(uuid)) {
				definition.setUuid(uuid);
			}
		}
		
		return definition;
	}
	
	@Override
	public AppointmentServiceDefinition save(AppointmentServiceDefinition instance) {
		return appointmentServiceService.save(instance);
	}
}
