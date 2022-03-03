package org.openmrs.module.initializer.api.idgen;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Columns specific to {@link SequentialIdentifierGenerator}.
 */
@OpenmrsProfile(modules = { "idgen:*" })
public class IdentifierPoolLineProcessor extends IdentifierSourceLineProcessor {
	
	@Autowired
	public IdentifierPoolLineProcessor(IdentifierSourceService idgenService) {
		super(idgenService);
	}
	
	@Override
	public IdgenSourceWrapper fill(IdgenSourceWrapper instance, CsvLine line) throws IllegalArgumentException {
		
		if (!IdentifierSourceType.POOL.equals(instance.getType())) {
			return instance;
		}
		
		IdentifierPool source = (IdentifierPool) instance.getIdentifierSource();
		
		// Pool Source
		String identifierSourceProvided = line.get(HEADER_POOL_IDENTIFIER_SOURCE, true);
		IdentifierSource poolSource = idgenService.getIdentifierSourceByUuid(identifierSourceProvided);
		if (poolSource == null) {
			throw new IllegalArgumentException("No identifier source found with uuid " + identifierSourceProvided);
		}
		source.setSource(poolSource);
		
		// Pool Batch Size
		String batchSizeProvided = line.get(HEADER_POOL_BATCH_SIZE);
		if (StringUtils.isNotEmpty(batchSizeProvided)) {
			try {
				source.setBatchSize(Integer.parseInt(batchSizeProvided));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Batch size must be a number.  Found " + batchSizeProvided);
			}
		}
		
		// Pool Min Size
		String minPoolSizeProvided = line.get(HEADER_POOL_MINIMUM_SIZE);
		if (StringUtils.isNotEmpty(minPoolSizeProvided)) {
			try {
				source.setMinPoolSize(Integer.parseInt(minPoolSizeProvided));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Min pool size must be a number.  Found " + batchSizeProvided);
			}
		}
		
		// Refill with scheduled task
		source.setRefillWithScheduledTask(line.getBool(HEADER_POOL_REFILL_WITH_TASK));
		
		// Sequential
		source.setSequential(line.getBool(HEADER_POOL_SEQUENTIAL_ALLOCATION));
		
		instance.setIdentifierSource(source);
		
		return instance;
	}
}
