package org.openmrs.module.initializer.api.idgen;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
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
		
		source.setUrl(getRequiredProperty(line, HEADER_URL));
		source.setUser(getRequiredProperty(line, HEADER_USER));
		source.setPassword(getRequiredProperty(line, HEADER_PASS));
		
		instance.setIdentifierSource(source);
		
		return instance;
	}

	/**
	 * Remote Identifier Sources have properties that contain sensitive information
	 * For this reason, we enable the ability to specify that these properties should
	 * be read from runtime or system properties, rather than fixed values in the CSV
	 */
	protected String getRequiredProperty(CsvLine line, String header) {
		String val = line.get(header, true);
		if (val == null) {
			throw new IllegalArgumentException(header + " is required");
		}
		if (val.toLowerCase().startsWith("property:")) {
			String property = val.substring(9);
			if (System.getProperties().containsKey(property)) {
				val = System.getProperty(property);
			} else {
				val = Context.getRuntimeProperties().getProperty(property);
			}
			if (val == null) {
				throw new IllegalArgumentException(header + " is required but property " + property + " is not found");
			}
		}
		return val;
	}
}
