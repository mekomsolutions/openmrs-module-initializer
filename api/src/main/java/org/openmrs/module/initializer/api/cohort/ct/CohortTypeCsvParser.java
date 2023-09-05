package org.openmrs.module.initializer.api.cohort.ct;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.cohort.CohortType;
import org.openmrs.module.cohort.api.CohortTypeService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "cohort:3.2.* - 9.*" })
public class CohortTypeCsvParser extends CsvParser<CohortType, BaseLineProcessor<CohortType>> {
	
	private CohortTypeService cohortTypeService;
	
	/**
	 * Most CSV parsers are built on a single line processor. This superclass constructor should be used
	 * to initialize such parsers.
	 * 
	 * @param lineProcessor The single line processor for the CSV parser.
	 */
	@Autowired
	protected CohortTypeCsvParser(@Qualifier("cohort.cohortTypeService") CohortTypeService cohortTypeService,
	    CohortTypeLineProcessor lineProcessor) {
		super(lineProcessor);
		this.cohortTypeService = cohortTypeService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.COHORT_TYPES;
	}
	
	@Override
	public CohortType bootstrap(CsvLine line) throws IllegalArgumentException {
		CohortType result = null;
		
		String name = line.getName();
		if (StringUtils.isNotBlank(name)) {
			result = cohortTypeService.getCohortTypeByName(name);
			
			if (result == null && BaseLineProcessor.getVoidOrRetire(line)) {
				result = cohortTypeService.getCohortTypeByName(name, true);
			}
		}
		
		if (result != null) {
			return result;
		}
		
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			result = cohortTypeService.getCohortTypeByUuid(uuid);
			
			if (result == null && BaseLineProcessor.getVoidOrRetire(line)) {
				result = cohortTypeService.getCohortTypeByUuid(uuid, true);
			}
		}
		
		if (result != null) {
			return result;
		}
		
		return new CohortType();
	}
	
	@Override
	public CohortType save(CohortType instance) {
		return cohortTypeService.saveCohortType(instance);
	}
}
