package org.openmrs.module.initializer.api.appt.servicetypes;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentServiceTypesCsvParser extends CsvParser<AppointmentServiceType, BaseLineProcessor<AppointmentServiceType>> {
	
	private AppointmentServiceDefinitionService service;
	
	private SessionFactory sessionFactory;
	
	@Autowired
	public AppointmentServiceTypesCsvParser(
	    @Qualifier("appointmentServiceService") AppointmentServiceDefinitionService service,
	    AppointmentServiceTypeLineProcessor processor, SessionFactory sessionFactory) {
		super(processor);
		this.service = service;
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.APPOINTMENT_SERVICE_TYPES;
	}
	
	@Override
	public AppointmentServiceType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		AppointmentServiceType type = service.getAppointmentServiceTypeByUuid(uuid);
		
		//		if (definition == null) {
		//			String name = line.getName(true); // should fail if name column missing
		//			definition = Utils.fetchBahmniAppointmentServiceDefinition(name, appointmentServiceService);
		//		}
		//		
		//		if (definition == null) {
		//			definition = new AppointmentServiceDefinition();
		//			if (!StringUtils.isEmpty(uuid)) {
		//				definition.setUuid(uuid);
		//			}
		//		}
		//		
		//		return definition;
		return type;
	}
	
	@Transactional
	@Override
	public AppointmentServiceType save(AppointmentServiceType instance) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(instance);
		return instance;
	}
}
