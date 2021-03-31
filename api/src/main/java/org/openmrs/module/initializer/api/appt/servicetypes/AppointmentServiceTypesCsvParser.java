package org.openmrs.module.initializer.api.appt.servicetypes;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentServiceTypesCsvParser extends CsvParser<AppointmentServiceType, BaseLineProcessor<AppointmentServiceType>> {
	
	private AppointmentServiceDefinitionService service;
	
	@Autowired
	public AppointmentServiceTypesCsvParser(
	    @Qualifier("appointmentServiceService") AppointmentServiceDefinitionService service,
	    AppointmentServiceTypeLineProcessor processor) {
		super(processor);
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.APPOINTMENT_SERVICE_TYPES;
	}
	
	@Override
	public AppointmentServiceType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		final String uuid = line.getUuid();
		
		AppointmentServiceType type = StringUtils.isEmpty(uuid) ? null : service.getAppointmentServiceTypeByUuid(uuid);
		
		if (type == null) {
			type = Utils.fetchBahmniAppointmentServiceType(line.getName(), service);
		}
		
		if (type == null) {
			type = new AppointmentServiceType();
		}
		
		if (!StringUtils.isEmpty(uuid)) {
			type.setUuid(uuid);
		}
		
		return type;
	}
	
	@Override
	public AppointmentServiceType save(AppointmentServiceType instance) {
		service.save(instance.getAppointmentServiceDefinition());
		return instance;
	}
}
