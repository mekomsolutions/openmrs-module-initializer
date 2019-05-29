package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypeLoader extends BaseCsvLoader<BaseAttributeType, AttributeTypeCsvParser> {
	
	@Autowired
	public void setParser(AttributeTypeCsvParser parser) {
		this.parser = parser;
	}
	
}
