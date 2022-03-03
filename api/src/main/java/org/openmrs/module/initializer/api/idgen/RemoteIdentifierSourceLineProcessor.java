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
	 * Attempts to read the value from the CSV line as usual or falls back to reading the specified
	 * system or runtime property. Eg "property:foo" will attempt to read first the system property
	 * named "foo" or eventually the runtime property named "foo". If the value is null / not defined,
	 * then this will throw an exception
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
				throw new IllegalArgumentException(header + " is required but property '" + property + "' is not found.");
			}
		}
		return val;
	}
}
