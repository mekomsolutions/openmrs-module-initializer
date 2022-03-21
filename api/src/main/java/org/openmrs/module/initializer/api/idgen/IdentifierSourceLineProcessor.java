package org.openmrs.module.initializer.api.idgen;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

/**
 * This is the first level line processor for identifier sources. It allows to parse and save
 * identifier sources with the minimal/common set of required fields.
 */
public abstract class IdentifierSourceLineProcessor extends BaseLineProcessor<IdgenSourceWrapper> {
	
	protected static String HEADER_IDTYPE = "Identifier type";
	
	/*
	 * headers specific to IdentifierPool
	 */
	protected static String HEADER_POOL_IDENTIFIER_SOURCE = "pool identifier source";
	
	protected static String HEADER_POOL_BATCH_SIZE = "pool refill batch size";
	
	protected static String HEADER_POOL_MINIMUM_SIZE = "pool minimum size";
	
	protected static String HEADER_POOL_REFILL_WITH_TASK = "pool refill with task";
	
	protected static String HEADER_POOL_SEQUENTIAL_ALLOCATION = "pool sequential allocation";
	
	/*
	 * headers specific to RemoteIdentifierSource
	 */
	protected static String HEADER_URL = "url";
	
	protected static String HEADER_USER = "user";
	
	protected static String HEADER_PASS = "password";
	
	/*
	 * headers specific to SequentialIdentifierGenerator
	 */
	protected static String HEADER_PREFIX = "prefix";
	
	protected static String HEADER_SUFFIX = "suffix";
	
	protected static String HEADER_FIRST_ID_BASE = "first identifier base";
	
	protected static String HEADER_MIN_LENGTH = "min length";
	
	protected static String HEADER_MAX_LENGTH = "max length";
	
	protected static String HEADER_BASE_CHAR_SET = "base character set";
	
	protected IdentifierSourceService idgenService;
	
	public IdentifierSourceLineProcessor(IdentifierSourceService idgenService) {
		this.idgenService = idgenService;
	}
	
	/**
	 * Fetches a patient identifier type by name or UUID.
	 * 
	 * @param identifier The name or the UUID.
	 */
	public static PatientIdentifierType getPatientIdentifierType(String identifier) {
		PatientService ps = Context.getPatientService();
		
		PatientIdentifierType pit = null;
		if (pit == null) {
			pit = ps.getPatientIdentifierTypeByName(identifier);
		}
		if (pit == null) {
			pit = ps.getPatientIdentifierTypeByUuid(identifier);
		}
		return pit;
	}
	
	/**
	 * Identifies the identifier source type from the CSV line.
	 */
	public static IdentifierSourceType getIdentifierSourceType(CsvLine line) throws IllegalArgumentException {
		
		if (line.get(HEADER_POOL_IDENTIFIER_SOURCE) != null || line.get(HEADER_POOL_BATCH_SIZE) != null
		        || line.get(HEADER_POOL_MINIMUM_SIZE) != null || line.get(HEADER_POOL_REFILL_WITH_TASK) != null
		        || line.get(HEADER_POOL_SEQUENTIAL_ALLOCATION) != null) {
			return IdentifierSourceType.POOL;
		}
		if (line.get(HEADER_PREFIX) != null || line.get(HEADER_SUFFIX) != null || line.get(HEADER_FIRST_ID_BASE) != null
		        || line.get(HEADER_MIN_LENGTH) != null || line.get(HEADER_MAX_LENGTH) != null
		        || line.get(HEADER_BASE_CHAR_SET) != null) {
			return IdentifierSourceType.SEQUENTIAL;
		}
		if (line.get(HEADER_URL) != null || line.get(HEADER_USER) != null || line.get(HEADER_PASS) != null) {
			return IdentifierSourceType.REMOTE;
		}
		
		throw new IllegalArgumentException("No identifier source type could be guessed from the CSV line: '" + line + "'.");
	}
	
	/**
	 * Identifies the identifier source type from an identifier source instance.
	 */
	public static IdentifierSourceType getIdentifierSourceType(IdentifierSource source) throws IllegalArgumentException {
		
		if (source instanceof IdentifierPool) {
			return IdentifierSourceType.POOL;
		}
		if (source instanceof RemoteIdentifierSource) {
			return IdentifierSourceType.REMOTE;
		}
		if (source instanceof SequentialIdentifierGenerator) {
			return IdentifierSourceType.SEQUENTIAL;
		}
		
		throw new IllegalArgumentException(
		        "No identifier source type could be guessed from the identifier source instance: '" + source.toString()
		                + "'.");
	}
}
