package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@Component
public class BaseAttributeTypeLineProcessor extends BaseLineProcessor<BaseAttributeType<?>> {
	
	protected static String HEADER_MIN_OCCURS = "min occurs";
	
	protected static String HEADER_MAX_OCCURS = "max occurs";
	
	protected static String HEADER_DATATYPE_CLASSNAME = "datatype classname";
	
	protected static String HEADER_DATATYPE_CONFIG = "datatype config";
	
	protected static String HEADER_PREFERRED_HANDLER_CLASSNAME = "preferred handler classname";
	
	protected static String HEADER_HANDLER_CONFIG = "handler config";
	
	@Override
	public BaseAttributeType<?> fill(BaseAttributeType<?> instance, CsvLine line) throws IllegalArgumentException {
		
		instance.setName(line.getName(true));
		instance.setDescription(line.get(HEADER_DESC));
		instance.setDatatypeClassname(line.get(HEADER_DATATYPE_CLASSNAME, true));
		instance.setDatatypeConfig(line.getString(HEADER_DATATYPE_CONFIG));
		instance.setMinOccurs(line.getInt(HEADER_MIN_OCCURS));
		instance.setMaxOccurs(line.getInt(HEADER_MAX_OCCURS));
		instance.setPreferredHandlerClassname(line.getString(HEADER_PREFERRED_HANDLER_CLASSNAME));
		instance.setHandlerConfig(line.getString(HEADER_HANDLER_CONFIG));
		
		return instance;
	}
}
