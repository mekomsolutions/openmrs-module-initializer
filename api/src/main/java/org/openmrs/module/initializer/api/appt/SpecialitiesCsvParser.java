package org.openmrs.module.initializer.api.appt;

import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SpecialitiesCsvParser extends CsvParser<Speciality, BaseLineProcessor<Speciality>> {
	
	private SpecialityService specialityService;
	
	@Autowired
	public SpecialitiesCsvParser(@Qualifier("specialityService") SpecialityService specialityService,
	    SpecialitiesLineProcessor processor) {
		super(processor);
		this.specialityService = specialityService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.SPECIALITIES;
	}
	
	@Override
	protected Speciality save(Speciality instance) {
		return specialityService.save(instance);
	}
}
