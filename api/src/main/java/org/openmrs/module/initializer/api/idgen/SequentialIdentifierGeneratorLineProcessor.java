package org.openmrs.module.initializer.api.idgen;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Columns specific to {@link SequentialIdentifierGenerator}.
 */
@OpenmrsProfile(modules = { "idgen:*" })
public class SequentialIdentifierGeneratorLineProcessor extends IdentifierSourceLineProcessor {
	
	@Autowired
	public SequentialIdentifierGeneratorLineProcessor(IdentifierSourceService idgenService) {
		super(idgenService);
	}
	
	@Override
	public IdgenSourceWrapper fill(IdgenSourceWrapper instance, CsvLine line) throws IllegalArgumentException {
		
		if (!IdentifierSourceType.SEQUENTIAL.equals(instance.getType())) {
			return instance;
		}
		
		SequentialIdentifierGenerator source = (SequentialIdentifierGenerator) instance.getIdentifierSource();
		
		source.setBaseCharacterSet(line.get(HEADER_BASE_CHAR_SET, true));
		source.setFirstIdentifierBase(line.get(HEADER_FIRST_ID_BASE, true));
		source.setPrefix(line.getString(HEADER_PREFIX, ""));
		source.setSuffix(line.getString(HEADER_SUFFIX, ""));
		source.setMinLength(line.getInt(HEADER_MIN_LENGTH));
		source.setMaxLength(line.getInt(HEADER_MAX_LENGTH));
		
		instance.setIdentifierSource(source);
		
		return instance;
	}
}
