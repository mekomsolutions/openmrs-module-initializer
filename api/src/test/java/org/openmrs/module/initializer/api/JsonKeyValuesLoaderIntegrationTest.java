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

import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.JsonKeyValuesLoader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class JsonKeyValuesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("personService")
	private PersonService ps;
	
	@Autowired
	private JsonKeyValuesLoader loader;
	
	@Before
	public void setup() {
		ConceptSource source = new ConceptSource();
		source.setName("Cambodia");
		source = cs.saveConceptSource(source);
		
		// A concept to be fetched via JKV
		{
			Concept c = new Concept();
			c.setUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			c.setFullySpecifiedName(new ConceptName("CONCEPT_FOR_FETCHING", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			{
				ConceptMapType mapType = cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
				Utils.ConceptMappingWrapper mappingWrapper = new Utils.ConceptMappingWrapper("Cambodia:123", mapType, cs);
				c.addConceptMapping(mappingWrapper.getConceptMapping());
			}
			cs.saveConcept(c);
		}
		
		// A person attr. type to be fetched via JKV
		{
			PersonAttributeType pat = new PersonAttributeType();
			pat.setUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			pat.setName("PAT_FOR_FETCHING");
			pat.setFormat("java.lang.String");
			ps.savePersonAttributeType(pat);
		}
	}
	
	@Test
	public void load_shouldFetchConceptsFromAllPossibleKeys() {
		// Replay
		loader.load();
		
		Concept c1 = getService().getConceptFromKey("impl.purpose.concept.uuid");
		Assert.assertNotNull(c1);
		Concept c2 = getService().getConceptFromKey("impl.purpose.concept.fsn");
		Assert.assertEquals(c1, c2);
		Concept c3 = getService().getConceptFromKey("impl.purpose.concept.mapping");
		Assert.assertEquals(c2, c3);
		
		Assert.assertNull(getService().getConceptFromKey("__invalid_json_key__"));
		Assert.assertEquals(c1, getService().getConceptFromKey("__invalid_json_key__", c1));
	}
	
	@Test
	public void load_shouldFetchPATFromAllPossibleKeys() {
		// Replay
		loader.load();
		
		PersonAttributeType pat1 = getService().getPersonAttributeTypeFromKey("impl.purpose.pat.uuid");
		Assert.assertNotNull(pat1);
		PersonAttributeType pat2 = getService().getPersonAttributeTypeFromKey("impl.purpose.pat.name");
		Assert.assertEquals(pat1, pat2);
		
		Assert.assertNull(getService().getPersonAttributeTypeFromKey("__invalid_json_key__"));
		Assert.assertEquals(pat1, getService().getPersonAttributeTypeFromKey("__invalid_json_key__", pat1));
	}
	
	@Test
	public void load_shouldLoadStructuredJsonValue() {
		// Replay
		loader.load();
		String json = getService().getValueFromKey("structured.json");
		
		// Verif
		Assert.assertEquals("{\"foo\":\"bar\",\"fooz\":{\"baz\":\"value\"}}", json);
	}
	
	@Test
	public void load_shouldLoadConceptList() {
		// Replay
		loader.load();
		List<Concept> concepts = getService().getConceptsFromKey("impl.purpose.concepts");
		
		// Verif
		Assert.assertThat(concepts.size(), is(2));
		for (Concept c : concepts) {
			Assert.assertNotNull(c);
		}
	}
}
