package org.openmrs.module.initializer.api.appt.specialities;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsSpecialitiesCsvParser extends CsvParser<Speciality, BaseLineProcessor<Speciality>> {
	
	private SpecialityService specialityService;
	
	@Autowired
	public AppointmentsSpecialitiesCsvParser(@Qualifier("specialityService") SpecialityService specialityService,
	    AppointmentsSpecialityLineProcessor processor) {
		super(processor);
		this.specialityService = specialityService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.APPOINTMENTS_SPECIALITIES;
	}
	
	@Override
	protected Speciality save(Speciality instance) {
		return specialityService.save(instance);
	}
}
