package org.openmrs.module.initializer.api.fhir.cpm;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ContactPoint;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirContactPointMap;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(modules = { "fhir2:1.6.* - 9.*" })
public class FhirContactPointMapLineProcessor extends BaseLineProcessor<FhirContactPointMap> {
	
	private static final String SYSTEM_HEADER = "system";
	private static final String USE_HEADER = "use";
	private static final String RANK_HEADER = "rank";
	@Override
	public FhirContactPointMap fill(FhirContactPointMap instance, CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		
		if (StringUtils.isNotBlank(uuid)) {
			instance.setUuid(line.getUuid());
		}
		
		// The Bootstrap method of the FhirContactPointMapCsvParser should set the attribute Type Domain.
		// If this has not happened, throw an exception
		if (instance.getAttributeTypeDomain() == null) {
			throw new IllegalArgumentException(
					"attribute type domain is missing from FHIR concept source " + instance.getUuid());
		}
		
		instance.setAttributeTypeDomain(instance.getAttributeTypeDomain());
		
		String system = line.get(SYSTEM_HEADER, true);
		String use = line.get(USE_HEADER, true);
		String rank = line.get(RANK_HEADER, true);
		
		boolean requiredFields = (instance.getId() == null || !BaseLineProcessor.getVoidOrRetire(line));
		if (requiredFields && (StringUtils.isBlank(system) || StringUtils.isBlank(rank) || StringUtils.isBlank(use))) {
			throw new IllegalStateException("system, rank, and use must be supplied");
		}
		
		instance.setSystem(ContactPoint.ContactPointSystem.valueOf(system));
		instance.setUse(ContactPoint.ContactPointUse.valueOf(use));
		instance.setRank(Integer.valueOf(rank));
		
		return instance;
	}
}
