package org.openmrs.module.initializer.api.idgen;

import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the first level line processor for identifier sources. It allows to parse and save
 * identifier sources with the minimal/common set of required fields.
 */
@OpenmrsProfile(modules = { "idgen:*" })
public class CommonIdentifierSourceLineProcessor extends IdentifierSourceLineProcessor {
	
	@Autowired
	public CommonIdentifierSourceLineProcessor(IdentifierSourceService idgenService) {
		super(idgenService);
	}
	
	@Override
	public IdgenSourceWrapper fill(IdgenSourceWrapper instance, CsvLine line) throws IllegalArgumentException {
		
		PatientIdentifierType idType = getPatientIdentifierType(line.getString(HEADER_IDTYPE));
		if (idType == null) {
			throw new IllegalArgumentException("No identifier type found for '" + line.get(HEADER_IDTYPE) + "'");
		}
		instance.getIdentifierSource().setIdentifierType(getPatientIdentifierType(line.getString(HEADER_IDTYPE)));
		instance.getIdentifierSource().setName(line.getString(HEADER_NAME));
		instance.getIdentifierSource().setDescription(line.getString(HEADER_DESC));
		
		return instance;
	}
}
