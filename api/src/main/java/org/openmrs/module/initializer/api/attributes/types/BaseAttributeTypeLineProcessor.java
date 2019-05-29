package org.openmrs.module.initializer.api.attributes.types;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseAttributeTypeLineProcessor extends BaseLineProcessor<BaseAttributeType> {
	
	// Generic headers
	protected static String HEADER_MIN_OCCURS = "min occurs";
	
	protected static String HEADER_MAX_OCCURS = "max occurs";
	
	protected static String HEADER_DATATYPE_CLASSNAME = "datatype classname";
	
	protected static String HEADER_DATATYPE_CONFIG = "datatype config";
	
	protected static String HEADER_PREFERRED_HANDLER_CLASSNAME = "preferred handler classname";
	
	protected static String HEADER_HANDLER_CONFIG = "handler config";
	
	public static String HEADER_DOMAIN = "domain";
	
	@Autowired
	private AttributeTypeLineProcessorCompatibilty handler;
	
	@Autowired
	private AttributeTypeServiceCompatibility service;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected BaseAttributeType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		BaseAttributeType attributeType = service.getAttributeTypeByUuid(uuid, handler.getAttributeType(line));
		if (attributeType == null) {
			attributeType = service.getAttributeTypeByName(line.get(HEADER_NAME), handler.getAttributeType(line));
			if (attributeType == null) {
				attributeType = handler.newAttributeType(line);
				if (StringUtils.isNotEmpty(uuid)) {
					attributeType.setUuid(uuid);
				}
			}
		}
		
		return attributeType;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected BaseAttributeType fill(BaseAttributeType instance, CsvLine line) throws IllegalArgumentException {
		
		instance.setName(line.get(HEADER_NAME));
		instance.setDescription(line.get(HEADER_DESC));
		instance.setDatatypeClassname(line.getString(HEADER_DATATYPE_CLASSNAME));
		instance.setDatatypeConfig(line.getString(HEADER_DATATYPE_CONFIG));
		instance.setMinOccurs(line.getInt(HEADER_MIN_OCCURS));
		instance.setMaxOccurs(line.getInt(HEADER_MAX_OCCURS));
		instance.setPreferredHandlerClassname(line.getString(HEADER_PREFERRED_HANDLER_CLASSNAME));
		instance.setHandlerConfig(line.getString(HEADER_HANDLER_CONFIG));
		
		return instance;
	}
	
	public void setHandler(AttributeTypeLineProcessorCompatibilty handler) {
		this.handler = handler;
	}
	
	public void setService(AttributeTypeServiceCompatibility service) {
		this.service = service;
	}
	
}
