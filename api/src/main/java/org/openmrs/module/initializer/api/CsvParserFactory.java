package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.c.ConceptsCsvParser;
import org.openmrs.module.initializer.api.drugs.DrugsCsvParser;
import org.openmrs.module.initializer.api.freq.OrderFrequenciesCsvParser;
import org.openmrs.module.initializer.api.idgen.IdentifierSourceCsvParser;
import org.openmrs.module.initializer.api.loc.LocationsCsvParser;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypesCsvParser;
import org.openmrs.module.initializer.api.program.ProgramCsvParser;
import org.openmrs.module.initializer.api.programworkflow.ProgramWorkflowCsvParser;

/**
 * Use this class to create a CSV parser based on a domain.
 */
public class CsvParserFactory {
	
	@SuppressWarnings("rawtypes")
	public static CsvParser create(InputStream is, String domain) throws IOException, IllegalArgumentException {
		
		if (InitializerConstants.DOMAIN_C.equals(domain)) {
			return new ConceptsCsvParser(is, Context.getConceptService());
		}
		
		if (InitializerConstants.DOMAIN_DRUGS.equals(domain)) {
			return new DrugsCsvParser(is, Context.getConceptService());
		}
		
		if (InitializerConstants.DOMAIN_FREQ.equals(domain)) {
			return new OrderFrequenciesCsvParser(is, Context.getOrderService());
		}
		
		if (InitializerConstants.DOMAIN_IDGEN.equals(domain)) {
			return new IdentifierSourceCsvParser(is, Context.getService(IdentifierSourceService.class));
		}
		
		if (InitializerConstants.DOMAIN_PAT.equals(domain)) {
			return new PersonAttributeTypesCsvParser(is, Context.getPersonService());
		}
		
		if (InitializerConstants.DOMAIN_LOC.equals(domain)) {
			return new LocationsCsvParser(is, Context.getLocationService());
		}
		
		if (InitializerConstants.DOMAIN_PROG.equals(domain)) {
			return new ProgramCsvParser(is, Context.getProgramWorkflowService());
		}
		
		if (InitializerConstants.DOMAIN_PROG_WF.equals(domain)) {
			return new ProgramWorkflowCsvParser(is, Context.getProgramWorkflowService());
		}
		
		throw new IllegalArgumentException(
		        "'" + domain + "' did not point to any identified CSV parser to process the input stream.");
	}
}
