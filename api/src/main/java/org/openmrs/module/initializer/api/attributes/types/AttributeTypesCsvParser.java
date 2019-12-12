package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypesCsvParser extends CsvParser<BaseAttributeType<?>, BaseLineProcessor<BaseAttributeType<?>>> {
	
	private AttributeTypesProxyService service;
	
	@Autowired
	public AttributeTypesCsvParser(AttributeTypesProxyService service, BaseAttributeTypeLineProcessor processor) {
		super(processor);
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ATTRIBUTE_TYPES;
	}
	
	@Override
	protected BaseAttributeType<?> save(BaseAttributeType<?> instance) {
		return service.saveAttributeType(instance);
	}
}
