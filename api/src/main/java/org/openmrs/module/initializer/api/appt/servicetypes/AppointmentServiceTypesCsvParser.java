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
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
	
	@Transactional
	@Override
	public AppointmentServiceType save(AppointmentServiceType instance) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(instance);
		return instance;
	}
}
