package org.openmrs.module.initializer.api.appt;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SpecialitiesLineProcessor extends BaseLineProcessor<Speciality> {
	
	private SpecialityService specialityService;
	
	@Autowired
	public SpecialitiesLineProcessor(@Qualifier("specialityService") SpecialityService specialityService) {
		this.specialityService = specialityService;
	}
	
	@Override
	protected Speciality bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		Speciality speciality = specialityService.getSpecialityByUuid(uuid);
		if (speciality == null) {
			speciality = new Speciality();
			if (!StringUtils.isEmpty(uuid)) {
				speciality.setUuid(uuid);
			}
		}
		
		return speciality;
	}
	
	@Override
	protected Speciality fill(Speciality speciality, CsvLine line) throws IllegalArgumentException {
		
		speciality.setName(line.get(HEADER_NAME));
		
		return speciality;
	}
}
