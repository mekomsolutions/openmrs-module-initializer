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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.freq.OrderFrequenciesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderFrequenciesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("orderService")
	private OrderService os;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;

	@Autowired
	@Qualifier("patientService")
	private PatientService ps;

	@Autowired
	@Qualifier("encounterService")
	private EncounterService es;

	@Autowired
	@Qualifier("locationService")
	private LocationService ls;

	@Autowired
	@Qualifier("providerService")
	private ProviderService providerService;

	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
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
		// An order frequency that should remain unchanged
		{
			Concept freqConcept = new Concept();
			freqConcept.setShortName(new ConceptName("Every 6 Hours", Locale.ENGLISH));
			freqConcept.setConceptClass(cs.getConceptClassByName("Frequency"));
			freqConcept.setDatatype(cs.getConceptDatatypeByName("N/A"));
			freqConcept = cs.saveConcept(freqConcept);

			OrderFrequency freq = new OrderFrequency();
			freq.setUuid("b1d7a778-bf25-11eb-8f35-0242ac110002");
			freq.setConcept(freqConcept);
			freq.setFrequencyPerDay(4.0);
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

	@Test
	public void load_shouldNotFailIfFrequenciesAreInUseAndUnchanged() throws Exception {

		// Add in some obs for the frequency that is unchanged by the metadata load
		OrderFrequency freq = os.getOrderFrequencyByUuid("b1d7a778-bf25-11eb-8f35-0242ac110002");

		PatientIdentifierType pit = new PatientIdentifierType();
		pit.setName("Test ID");
		ps.savePatientIdentifierType(pit);

		EncounterType et = new EncounterType();
		et.setName("Test Encounter Type");
		es.saveEncounterType(et);

		Provider provider = new Provider();
		provider.setPerson(userService.getAllUsers().get(0).getPerson());
		providerService.saveProvider(provider);

		Patient p = new Patient();
		p.setGender("M");
		p.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1985-05-22"));
		p.addName(new PersonName("John", "Test", "Smith"));
		p.addIdentifier(new PatientIdentifier("12345", pit, ls.getDefaultLocation()));
		ps.savePatient(p);

		Encounter e = new Encounter();
		e.setPatient(p);
		e.setEncounterType(et);
		e.setEncounterDatetime(new SimpleDateFormat("yyyy-MM-dd").parse("1995-04-11"));

		DrugOrder drugOrder = new DrugOrder();
		drugOrder.setCareSetting(os.getCareSettingByName("INPATIENT"));
		drugOrder.setEncounter(e);
		drugOrder.setPatient(p);
		drugOrder.setConcept(hourlyConcept);
		drugOrder.setOrderer(provider);
		drugOrder.setDosingType(FreeTextDosingInstructions.class);
		drugOrder.setDosingInstructions("Test Drug Order");
		drugOrder.setFrequency(freq);
		e.addOrder(drugOrder);

		es.saveEncounter(e);

		loader.loadUnsafe(new ArrayList<>(), true);
	}
}
