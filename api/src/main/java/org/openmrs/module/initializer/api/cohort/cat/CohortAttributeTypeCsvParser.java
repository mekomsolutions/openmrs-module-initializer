package org.openmrs.module.initializer.api.cohort.cat;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.cohort.CohortAttributeType;
import org.openmrs.module.cohort.api.CohortService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "cohort:3.2.* - 9.*" })
public class CohortAttributeTypeCsvParser extends CsvParser<CohortAttributeType, BaseLineProcessor<CohortAttributeType>> {
	
	private CohortService cohortService;
	
	@Override
	public Domain getDomain() {
		return Domain.COHORT_ATTRIBUTE_TYPES;
	}
	
	/**
	 * Most CSV parsers are built on a single line processor. This superclass constructor should be used
	 * to initialize such parsers.
	 * 
	 * @param lineProcessor The single line processor for the CSV parser.
	 */
	@Autowired
	protected CohortAttributeTypeCsvParser(@Qualifier("cohort.cohortService") CohortService cohortService,
	    CohortAttributeTypeLineProcessor lineProcessor) {
		super(lineProcessor);
		this.cohortService = cohortService;
	}
	
	@Override
	public CohortAttributeType bootstrap(CsvLine line) throws IllegalArgumentException {
		CohortAttributeType result = null;
		
		String name = line.getName();
		if (StringUtils.isNotBlank(name)) {
			result = cohortService.getAttributeTypeByName(name);
		}
		
		if (result != null) {
			return result;
		}
		
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			result = cohortService.getAttributeTypeByUuid(uuid);
		}
		
		if (result != null) {
			return result;
		}
		
		return new CohortAttributeType();
	}
	
	@Override
	public CohortAttributeType save(CohortAttributeType instance) {
		return cohortService.saveAttributeType(instance);
	}
}
