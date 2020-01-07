package org.openmrs.module.initializer.api.attributes.types;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypesCsvParser extends CsvParser<BaseAttributeType<?>, BaseLineProcessor<BaseAttributeType<?>>> {
	
	private AttributeTypesProxyService service;
	
	private AttributeTypeCsvLineHandler handler;
	
	@Autowired
	public AttributeTypesCsvParser(AttributeTypesProxyService service, BaseAttributeTypeLineProcessor processor,
	    AttributeTypeCsvLineHandler handler) {
		super(processor);
		this.service = service;
		this.handler = handler;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ATTRIBUTE_TYPES;
	}
	
	@Override
	public BaseAttributeType<?> bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		BaseAttributeType<?> attributeType = service.getAttributeTypeByUuid(uuid, handler.getAttributeType(line));
		
		if (attributeType == null) {
			attributeType = service.getAttributeTypeByName(line.getName(true), handler.getAttributeType(line));
			if (attributeType == null) {
				attributeType = handler.newAttributeType(line);
				if (StringUtils.isNotEmpty(uuid)) {
					attributeType.setUuid(uuid);
				}
			}
		}
		
		return attributeType;
	}
	
	@Override
	public BaseAttributeType<?> save(BaseAttributeType<?> instance) {
		return service.saveAttributeType(instance);
	}
}
