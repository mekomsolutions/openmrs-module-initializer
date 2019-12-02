package org.openmrs.module.initializer.api.datafilter.mappings;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "datafilter:*" })
public class DataFilterMappingsCsvParser extends CsvParser<DataFilterMapping, BaseLineProcessor<DataFilterMapping>> {
	
	@Autowired
	public DataFilterMappingsCsvParser(DataFilterMappingLineProcessor processor) {
		super(processor);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.DATAFILTER_MAPPINGS;
	}
	
	@Override
	protected DataFilterMapping save(DataFilterMapping mapping) {
		mapping.setId(1); // this marks it as "saved"
		return mapping;
	}
}
