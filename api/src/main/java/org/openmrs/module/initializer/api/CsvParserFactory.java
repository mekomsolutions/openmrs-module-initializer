package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.c.ConceptsCsvParser;
import org.openmrs.module.initializer.api.drugs.DrugsCsvParser;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypesCsvParser;

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
		
		if (InitializerConstants.DOMAIN_PAT.equals(domain)) {
			return new PersonAttributeTypesCsvParser(is, Context.getPersonService());
		}
		
		throw new IllegalArgumentException("'" + domain
		        + "' did not point to any identified CSV parser to process the input stream.");
	}
}
