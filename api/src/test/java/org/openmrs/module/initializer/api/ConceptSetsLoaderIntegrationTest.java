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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c.ConceptSetsLoader;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConceptSetsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private ConceptsLoader conceptsLoader;
	
	@Autowired
	private ConceptSetsLoader conceptSetsLoader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
		executeDataSet("testdata/test-concepts-numeric.xml");
	}
	
	@Test
	public void load_shouldLoadConceptSetsAccordingToCsvFiles() {
		
		// Load
		conceptsLoader.load();
		conceptSetsLoader.load();
		
		{
			Concept senseSet = cs.getConceptByUuid("54014540-311d-11ec-8d2b-0242ac110002");
			assertNotNull(senseSet);
			assertEquals(5, senseSet.getSetMembers().size());
			List<ConceptSet> senseMembers = new ArrayList<>(senseSet.getConceptSets());
			Collections.sort(senseMembers);
			assertEquals("Smell", senseMembers.get(0).getConcept().getName().getName());
			assertEquals(new Double(1.0), senseMembers.get(0).getSortWeight());
			assertEquals("Taste", senseMembers.get(1).getConcept().getName().getName());
			assertEquals(new Double(2.0), senseMembers.get(1).getSortWeight());
			assertEquals("Sight", senseMembers.get(2).getConcept().getName().getName());
			assertEquals(new Double(3.0), senseMembers.get(2).getSortWeight());
			assertEquals("Touch", senseMembers.get(3).getConcept().getName().getName());
			assertEquals(new Double(4.0), senseMembers.get(3).getSortWeight());
			assertEquals("Sound", senseMembers.get(4).getConcept().getName().getName());
			assertEquals(new Double(50.0), senseMembers.get(4).getSortWeight());
		}
		
		{
			Concept senseQuestion = cs.getConceptByUuid("5a393db9-311d-11ec-8d2b-0242ac110002");
			assertNotNull(senseQuestion);
			List<ConceptAnswer> senseAnswers = new ArrayList<>(senseQuestion.getAnswers());
			Collections.sort(senseAnswers);
			assertEquals(3, senseAnswers.size());
			assertEquals("Smell", senseAnswers.get(0).getAnswerConcept().getName().getName());
			assertEquals(new Double(1), senseAnswers.get(0).getSortWeight()); // Auto-set by concept api
			assertEquals("Sound", senseAnswers.get(1).getAnswerConcept().getName().getName());
			assertEquals(new Double(100), senseAnswers.get(1).getSortWeight());
			assertEquals("Sight", senseAnswers.get(2).getAnswerConcept().getName().getName());
			assertEquals(new Double(200), senseAnswers.get(2).getSortWeight());
		}
	}
}
