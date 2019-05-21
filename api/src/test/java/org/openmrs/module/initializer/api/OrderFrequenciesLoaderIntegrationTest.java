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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.OrderFrequency;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.freq.OrderFrequenciesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class OrderFrequenciesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("orderService")
	private OrderService os;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private OrderFrequenciesLoader loader;
	
	private Concept hourlyConcept;
	
	private Concept bidailyConcept;
	
	@Before
	public void setup() {
		
		// Concepts to be used as a 'frequency'
		{
			hourlyConcept = new Concept();
			hourlyConcept.setShortName(new ConceptName("Hourly", Locale.ENGLISH));
			hourlyConcept.setConceptClass(cs.getConceptClassByName("Frequency"));
			hourlyConcept.setDatatype(cs.getConceptDatatypeByName("N/A"));
			hourlyConcept = cs.saveConcept(hourlyConcept);
		}
		{
			bidailyConcept = new Concept();
			bidailyConcept.setShortName(new ConceptName("Bidaily", Locale.ENGLISH));
			bidailyConcept.setConceptClass(cs.getConceptClassByName("Frequency"));
			bidailyConcept.setDatatype(cs.getConceptDatatypeByName("N/A"));
			bidailyConcept = cs.saveConcept(bidailyConcept);
		}
		
		// An order frequency to be edited
		{
			Concept freqConcept = new Concept();
			freqConcept.setShortName(new ConceptName("Weekly", Locale.ENGLISH));
			freqConcept.setConceptClass(cs.getConceptClassByName("Frequency"));
			freqConcept.setDatatype(cs.getConceptDatatypeByName("N/A"));
			freqConcept = cs.saveConcept(freqConcept);
			
			OrderFrequency freq = new OrderFrequency();
			freq.setUuid("136ebdb7-e989-47cf-8ec2-4e8b2ffe0ab3");
			freq.setConcept(freqConcept);
			freq.setFrequencyPerDay(1.0);
			freq = os.saveOrderFrequency(freq);
		}
		// An order frequency to be retired
		{
			Concept freqConcept = new Concept();
			freqConcept.setShortName(new ConceptName("Monthly", Locale.ENGLISH));
			freqConcept.setConceptClass(cs.getConceptClassByName("Frequency"));
			freqConcept.setDatatype(cs.getConceptDatatypeByName("N/A"));
			freqConcept = cs.saveConcept(freqConcept);
			
			OrderFrequency freq = new OrderFrequency();
			freq.setUuid("4b33b729-1fe3-4fa5-acc4-084beb069b68");
			freq.setConcept(freqConcept);
			freq.setFrequencyPerDay(1.0);
			freq = os.saveOrderFrequency(freq);
		}
	}
	
	@Test
	public void load_shouldLoadOrderFrequenciesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// created frequency
		{
			OrderFrequency freq = os.getOrderFrequencyByConcept(hourlyConcept);
			Assert.assertNotNull(freq);
			Assert.assertEquals(0, Double.compare(24.0, freq.getFrequencyPerDay()));
		}
		
		// retired frequency
		{
			OrderFrequency freq = os.getOrderFrequencyByUuid("4b33b729-1fe3-4fa5-acc4-084beb069b68");
			Assert.assertNotNull(freq);
			Assert.assertTrue(freq.getRetired());
		}
		
		// edited frequency
		{
			OrderFrequency freq = os.getOrderFrequencyByUuid("136ebdb7-e989-47cf-8ec2-4e8b2ffe0ab3");
			Assert.assertNotNull(freq);
			Assert.assertEquals(bidailyConcept, freq.getConcept());
			Assert.assertEquals(0, Double.compare(0.5, freq.getFrequencyPerDay()));
		}
	}
}
