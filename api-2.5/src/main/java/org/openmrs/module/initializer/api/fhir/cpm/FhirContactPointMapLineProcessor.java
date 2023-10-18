package org.openmrs.module.initializer.api.fhir.cpm;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ContactPoint;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirContactPointMap;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

import static org.openmrs.module.initializer.api.fhir.cpm.FhirContactPointMapCsvParser.ATTRIBUTE_TYPE_DOMAIN_HEADER;

@OpenmrsProfile(modules = { "fhir2:1.11.* - 9.*" }, openmrsPlatformVersion = "2.5.13 - 2.5.*, 2.6.2 - 2.6.*, 2.7.* - 9.*")
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
		
		line.get(ATTRIBUTE_TYPE_DOMAIN_HEADER, true);
		
		String system = line.get(SYSTEM_HEADER, false);
		String use = line.get(USE_HEADER, false);
		String rank = line.get(RANK_HEADER, false);
		
		if (system != null) {
			instance.setSystem(ContactPoint.ContactPointSystem.valueOf(system));
		}
		
		if (use != null) {
			instance.setUse(ContactPoint.ContactPointUse.valueOf(use));
		}
		
		if (rank != null) {
			int rankInt = Integer.parseInt(rank);
			if (rankInt < 1) {
				throw new IllegalArgumentException("Rank must be a positive integer, i.e., 1+");
			}
			instance.setRank(rankInt);
		}
		
		return instance;
	}
}
