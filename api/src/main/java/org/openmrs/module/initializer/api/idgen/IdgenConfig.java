package org.openmrs.module.initializer.api.idgen;

import java.util.Collections;
import java.util.List;

import org.openmrs.module.idgen.IdentifierSource;

public class IdgenConfig {
	
	protected List<IdentifierSource> identifierSources = Collections.emptyList();
	
	public List<IdentifierSource> getIdentifierSources() {
		return identifierSources;
	}
}
