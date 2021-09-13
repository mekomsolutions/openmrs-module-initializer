package org.openmrs.module.initializer.api.c;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptsCsvParser extends CsvParser<Concept, BaseLineProcessor<Concept>> {
	
	private ConceptService conceptService;
	
	private ConceptNumericLineProcessor numericProcessor;
	
	private ConceptComplexLineProcessor complexProcessor;
	
	private NestedConceptLineProcessor nestedProcessor;
	
	private MappingsConceptLineProcessor mappingsProcessor;
	
	private ConceptAttributeLineProcessor conceptAttributeLineProcessor;
	
	@Autowired
	public ConceptsCsvParser(@Qualifier("conceptService") ConceptService conceptService,
	    @Qualifier("initializer.conceptLineProcessor") ConceptLineProcessor baseProcessor,
	    @Qualifier("initializer.conceptNumericLineProcessor") ConceptNumericLineProcessor numericProcessor,
	    @Qualifier("initializer.conceptComplexLineProcessor") ConceptComplexLineProcessor complexProcessor,
	    @Qualifier("initializer.nestedConceptLineProcessor") NestedConceptLineProcessor nestedProcessor,
	    @Qualifier("initializer.mappingsConceptLineProcessor") MappingsConceptLineProcessor mappingsProcessor,
	    @Qualifier("initializer.conceptAttributeLineProcessor") ConceptAttributeLineProcessor conceptAttributeLineProcessor) {
		
		super(baseProcessor);
		this.numericProcessor = numericProcessor;
		this.complexProcessor = complexProcessor;
		this.nestedProcessor = nestedProcessor;
		this.mappingsProcessor = mappingsProcessor;
		this.conceptAttributeLineProcessor = conceptAttributeLineProcessor;
		
		this.conceptService = conceptService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPTS;
	}
	
	@Override
	public Concept bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		Concept concept = conceptService.getConceptByUuid(uuid);
		
		if (StringUtils.isEmpty(uuid) && concept == null) {
			Locale currentLocale = Context.getLocale();
			LocalizedHeader lh = LocalizedHeader.getLocalizedHeader(line.getHeaderLine(),
			    ConceptLineProcessor.HEADER_FSNAME);
			for (Locale nameLocale : lh.getLocales()) {
				String name = line.get(lh.getI18nHeader(nameLocale));
				if (!StringUtils.isEmpty(name)) {
					Context.setLocale(nameLocale);
					concept = conceptService.getConceptByName(name);
					if (concept != null) {
						break;
					}
				}
			}
			Context.setLocale(currentLocale);
		}
		
		if (concept == null) {
			concept = new Concept();
			if (!StringUtils.isEmpty(uuid)) {
				concept.setUuid(uuid);
			}
		}
		
		return concept;
	}
	
	@Override
	public Concept save(Concept instance) {
		return conceptService.saveConcept(instance);
	}
	
	@Override
	protected void setLineProcessors(String version) {
		lineProcessors.clear();
		
		lineProcessors.add(numericProcessor);
		lineProcessors.add(complexProcessor);
		lineProcessors.add(getSingleLineProcessor());
		lineProcessors.add(nestedProcessor);
		lineProcessors.add(mappingsProcessor);
		lineProcessors.add(conceptAttributeLineProcessor);
	}
}
