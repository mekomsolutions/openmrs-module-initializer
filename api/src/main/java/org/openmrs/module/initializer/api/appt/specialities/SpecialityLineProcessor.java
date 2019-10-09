package org.openmrs.module.initializer.api.appt.specialities;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This is the first level line processor for specialities. It allows to parse and save specialities
 * with the minimal set of required fields.
 */
@Component
public class SpecialityLineProcessor extends BaseLineProcessor<Speciality> {
	
	private SpecialityService specialityservice;
	
	@Autowired
	public SpecialityLineProcessor(@Qualifier("specialityService") SpecialityService specialityservice) {
		super();
		this.specialityservice = specialityservice;
	}
	
	@Override
	protected Speciality bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		Speciality speciality = specialityservice.getSpecialityByUuid(uuid); // unfortunately, there is no #getSpecialityByName
		if (speciality == null) {
			speciality = new Speciality();
			if (!StringUtils.isEmpty(uuid)) {
				speciality.setUuid(uuid);
			}
		}
		
		return speciality;
	}
	
	public Speciality fill(Speciality speciality, CsvLine line) throws IllegalArgumentException {
		
		String specialityName = line.get(HEADER_NAME, true); // should fail is name column missing
		if (specialityName == null) {
			throw new IllegalArgumentException("A speciality must at least be provided a name: '" + line.toString() + "'");
		}
		speciality.setName(specialityName);
		
		return speciality;
	}
}
