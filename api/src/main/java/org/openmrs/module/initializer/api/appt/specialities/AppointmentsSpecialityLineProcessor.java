package org.openmrs.module.initializer.api.appt.specialities;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for specialities. It allows to parse and save specialities
 * with the minimal set of required fields.
 */
@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsSpecialityLineProcessor extends BaseLineProcessor<Speciality> {
	
	private SpecialityService specialityservice;
	
	@Autowired
	public AppointmentsSpecialityLineProcessor(@Qualifier("specialityService") SpecialityService specialityservice) {
		super();
		this.specialityservice = specialityservice;
	}
	
	@Override
	protected Speciality bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		Speciality speciality = specialityservice.getSpecialityByUuid(uuid);
		
		if (speciality == null) {
			String specialityName = line.get(HEADER_NAME, true); // should fail is name column missing
			for (Speciality currentSpeciality : specialityservice.getAllSpecialities()) {
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
	
	public Speciality fill(Speciality speciality, CsvLine line) throws IllegalArgumentException {
		
		String specialityName = line.get(HEADER_NAME, true); // should fail is name column missing
		if (StringUtils.isEmpty(specialityName)) {
			throw new IllegalArgumentException("A speciality must at least be provided a name: '" + line.toString() + "'");
		}
		speciality.setName(specialityName);
		
		return speciality;
	}
}
