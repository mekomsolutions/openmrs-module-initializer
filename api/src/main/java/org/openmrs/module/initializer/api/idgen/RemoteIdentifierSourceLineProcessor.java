package org.openmrs.module.initializer.api.idgen;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Columns specific to {@link SequentialIdentifierGenerator}.
 */
@OpenmrsProfile(modules = { "idgen:*" })
public class RemoteIdentifierSourceLineProcessor extends IdentifierSourceLineProcessor {
	
	@Autowired
	public RemoteIdentifierSourceLineProcessor(IdentifierSourceService idgenService) {
		super(idgenService);
	}
	
	@Override
	public IdgenSourceWrapper fill(IdgenSourceWrapper instance, CsvLine line) throws IllegalArgumentException {
		
		if (!IdentifierSourceType.REMOTE.equals(instance.getType())) {
			return instance;
		}
		
		RemoteIdentifierSource source = (RemoteIdentifierSource) instance.getIdentifierSource();
		
		source.setUrl(line.get(HEADER_URL, true));
		source.setUser(line.get(HEADER_USER, true));
		source.setPassword(line.get(HEADER_PASS, true));
		
		instance.setIdentifierSource(source);
		
		return instance;
	}
}
