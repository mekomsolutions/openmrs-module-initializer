/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class InitializerServiceIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("initializer.InitializerService")
	InitializerService initializerService;
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	@Qualifier("adminService")
	AdministrationService administrationService;
	
	@Test
	public void getUnretiredConceptsByFullySpecifiedName_shouldReturnExactNameCaseInsensitive() {
		ConceptDatatype numeric = conceptService.getConceptDatatypeByName("Numeric");
		ConceptClass finding = conceptService.getConceptClassByName("Finding");
		Locale enGb = new Locale("en", "GB");
		
		Concept cd4Count = conceptService.getConcept(5497);
		assertEquals("CD4 COUNT", cd4Count.getFullySpecifiedName(enGb).getName());
		
		administrationService.setGlobalProperty(OpenmrsConstants.GP_CASE_SENSITIVE_DATABASE_STRING_COMPARISON, "true");
		
		List<Concept> concepts = initializerService.getUnretiredConceptsByFullySpecifiedName("cd4 Count");
		assertEquals(1, concepts.size());
		assertEquals(cd4Count, concepts.get(0));
		
		Concept cd4Percent = new Concept();
		cd4Percent.setDatatype(numeric);
		cd4Percent.setConceptClass(finding);
		cd4Percent.setFullySpecifiedName(new ConceptName("cd4%", enGb));
		conceptService.saveConcept(cd4Percent);
		
		concepts = initializerService.getUnretiredConceptsByFullySpecifiedName("CD4%");
		assertEquals(1, concepts.size());
		assertEquals(cd4Percent, concepts.get(0));
	}
}
