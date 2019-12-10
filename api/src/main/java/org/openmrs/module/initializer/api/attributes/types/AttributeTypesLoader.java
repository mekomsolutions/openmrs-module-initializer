package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class AttributeTypesLoader extends BaseCsvLoader<BaseAttributeType, AttributeTypesCsvParser> {
	
	@Autowired
	public void setParser(AttributeTypesCsvParser parser) {
		this.parser = parser;
	}
}
