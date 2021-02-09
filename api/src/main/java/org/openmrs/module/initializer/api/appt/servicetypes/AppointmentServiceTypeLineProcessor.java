package org.openmrs.module.initializer.api.appt.servicetypes;

import java.util.Set;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for Bahmni Appointment Service Types. It allows to parse
 * and save Appointment Service Types with the minimal set of required fields.
 */
@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentServiceTypeLineProcessor extends BaseLineProcessor<AppointmentServiceType> {
	
	public final static String HEADER_SERVICE_DEFINITION = "service definition";
	
	private AppointmentServiceDefinitionService service;
	
	@Autowired
	public AppointmentServiceTypeLineProcessor(
	    @Qualifier("appointmentServiceService") AppointmentServiceDefinitionService service) {
		super();
		this.service = service;
	}
	
	@Override
	public AppointmentServiceType fill(AppointmentServiceType type, CsvLine line) throws IllegalArgumentException {
		
		type.setName(line.get(HEADER_NAME, true));
		type.setDuration(line.getInt(HEADER_DURATION));
		
		AppointmentServiceDefinition def = Utils
		        .fetchBahmniAppointmentServiceDefinition(line.getString(HEADER_SERVICE_DEFINITION), service);
		AppointmentServiceDefinition prevDef = type.getAppointmentServiceDefinition();
		//		if (!Utils.equals(def, prevDef)) {
		{
			//				Set<AppointmentServiceType> types = prevDef.getServiceTypes();
			//				types.remove(type);
			//				prevDef.setServiceTypes(types);
			//				type.setAppointmentServiceDefinition(null);
			//				service.save(prevDef);
		}
		{
			Set<AppointmentServiceType> types = def.getServiceTypes();
			types.add(type);
			def.setServiceTypes(types);
			type.setAppointmentServiceDefinition(def);
		}
		//		}
		
		return type;
	}
}
