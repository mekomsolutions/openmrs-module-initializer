package org.openmrs.module.initializer.api.idgen;

import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.CsvLine;

/**
 * Columns specific to {@link SequentialIdentifierGenerator}.
 */
public class SequentialIdentifierGeneratorLineProcessor extends BaseIdentifierSourceLineProcessor {
	
	public SequentialIdentifierGeneratorLineProcessor(String[] headerLine, IdentifierSourceService is) {
		super(headerLine, is);
	}
	
	protected IdgenSourceWrapper fill(IdgenSourceWrapper instance, CsvLine line) throws IllegalArgumentException {
		
		if (!IdentifierSourceType.SEQUENTIAL.equals(instance.getType())) {
			return instance;
		}
		
		((SequentialIdentifierGenerator) instance.getIdentifierSource())
		        .setFirstIdentifierBase(line.get(HEADER_FIRST_ID_BASE));
		((SequentialIdentifierGenerator) instance.getIdentifierSource()).setPrefix(line.getString(HEADER_PREFIX, ""));
		((SequentialIdentifierGenerator) instance.getIdentifierSource()).setSuffix(line.getString(HEADER_SUFFIX, ""));
		((SequentialIdentifierGenerator) instance.getIdentifierSource()).setMinLength(line.getInt(HEADER_MIN_LENGTH));
		((SequentialIdentifierGenerator) instance.getIdentifierSource()).setMaxLength(line.getInt(HEADER_MAX_LENGTH));
		((SequentialIdentifierGenerator) instance.getIdentifierSource()).setBaseCharacterSet(line.get(HEADER_BASE_CHAR_SET));
		
		return instance;
	}
}
