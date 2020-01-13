package org.openmrs.module.initializer.api.appt.specialities;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
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
	public Speciality bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		String specialityName = line.getName(true); // should fail is name column missing
		
		Speciality speciality = specialityService.getSpecialityByUuid(uuid);
		
		if (speciality == null) {
			for (Speciality currentSpeciality : specialityService.getAllSpecialities()) {
				if (currentSpeciality.getName().equalsIgnoreCase(specialityName)) {
					speciality = currentSpeciality;
				}
			}
		}
		
		if (speciality == null) {
			speciality = new Speciality();
			if (!StringUtils.isEmpty(uuid)) {
				speciality.setUuid(uuid);
			}
		}
		
		return speciality;
	}
	
	@Override
	public Speciality save(Speciality instance) {
		return specialityService.save(instance);
	}
}
