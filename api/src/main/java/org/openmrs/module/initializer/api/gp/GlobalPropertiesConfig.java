package org.openmrs.module.initializer.api.gp;

import java.util.Collections;
import java.util.List;

import org.openmrs.GlobalProperty;

public class GlobalPropertiesConfig {
	
	protected List<GlobalProperty> globalProperties = Collections.emptyList();
	
	public List<GlobalProperty> getGlobalProperties() {
		return globalProperties;
	}
}
