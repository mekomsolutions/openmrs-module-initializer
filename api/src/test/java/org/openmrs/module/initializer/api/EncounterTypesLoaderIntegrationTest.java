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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.EncounterType;
import org.openmrs.Privilege;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.et.EncounterTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EncounterTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("encounterService")
	private EncounterService es;
	
	@Autowired
	@Qualifier("userService")
	private UserService us;
	
	@Autowired
	private EncounterTypesLoader loader;
	
	private Locale localeEn = Locale.ENGLISH;
	
	private Locale localeKm = new Locale("km", "KH");
	
	@BeforeEach
	public void setup() {
		
		// a couple of privileges
		{
			us.savePrivilege(new Privilege("Can: View X-ray encounter"));
			us.savePrivilege(new Privilege("Can: Edit X-ray encounter"));
			us.savePrivilege(new Privilege("Can: View oncology encounter"));
		}
		// an encounter type to retire
		{
			EncounterType et = new EncounterType("Encounter To Retire", "This one should be retired after loading.");
			et.setUuid("bed6f0f6-ab07-481f-929f-3d26e6cb1138");
			es.saveEncounterType(et);
		}
		// an encounter type to edit
		{
			EncounterType et = new EncounterType("Oncology Encounter", "A old description for the oncology encounter.");
			et.setViewPrivilege(us.savePrivilege(new Privilege("Can: View histology encounter")));
			es.saveEncounterType(et);
		}
	}
	
	@Test
	public void load_shouldLoadEncounterTypesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// verif
		{
			EncounterType et = es.getEncounterType("Triage Encounter");
			Assertions.assertNotNull(et);
			Assertions.assertEquals("An encounter for triaging patients.", et.getDescription());
			Assertions.assertNull(et.getViewPrivilege());
			Assertions.assertNull(et.getEditPrivilege());
		}
		{
			EncounterType et = es.getEncounterTypeByUuid("aaa1a367-3047-4833-af27-b30e2dac9028");
			Assertions.assertNotNull(et);
			Assertions.assertEquals("Medical History Encounter", et.getName());
			Assertions.assertEquals("An interview about the patient medical history.", et.getDescription());
			Assertions.assertNull(et.getViewPrivilege());
			Assertions.assertNull(et.getEditPrivilege());
		}
		{
			EncounterType et = es.getEncounterTypeByUuid("439559c2-a3a4-4a25-b4b2-1a0299e287ee");
			Assertions.assertNotNull(et);
			Assertions.assertEquals("X-ray Encounter", et.getName());
			Assertions.assertEquals("An encounter during wich X-rays are performed on the patient.", et.getDescription());
			Assertions.assertEquals(us.getPrivilege("Can: View X-ray encounter"), et.getViewPrivilege());
			Assertions.assertEquals(us.getPrivilege("Can: Edit X-ray encounter"), et.getEditPrivilege());
		}
		{
			Assertions.assertNull(es.getEncounterTypeByUuid("400d7e07-6de6-40ac-8611-dcce12408e71"));
			Assertions.assertNull(es.getEncounterType("Foo Encounter"));
		}
		{
			EncounterType et = es.getEncounterTypeByUuid("bed6f0f6-ab07-481f-929f-3d26e6cb1138");
			Assertions.assertNotNull(et);
			Assertions.assertTrue(et.isRetired());
		}
		{
			EncounterType et = es.getEncounterType("Oncology Encounter");
			Assertions.assertNotNull(et);
			Assertions.assertEquals("A new description for the oncology encounter.", et.getDescription());
			Assertions.assertEquals(us.getPrivilege("Can: View oncology encounter"), et.getViewPrivilege());
		}
		// verify EncounterTypes domain i18n on entries with display:xy fields
		{
			Assertions.assertEquals("Medical History Encounter (translated)", Context.getMessageSourceService()
			        .getMessage("ui.i18n.EncounterType.name.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeEn));
			Assertions.assertEquals("ប្រវត្តិសាស្រ្តវេជ្ជសាស្រ្ត", Context.getMessageSourceService()
			        .getMessage("ui.i18n.EncounterType.name.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeKm));
			Assertions.assertEquals("Medical History Encounter (translated)", Context.getMessageSourceService()
			        .getMessage("org.openmrs.EncounterType.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeEn));
			Assertions.assertEquals("ប្រវត្តិសាស្រ្តវេជ្ជសាស្រ្ត", Context.getMessageSourceService()
			        .getMessage("org.openmrs.EncounterType.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeKm));
		}
		// verify no EncounterTypes domain i18n on entries without display:xy fields and entries without pre-filled uuids
		{
			Assertions.assertNotNull(es.getEncounterTypeByUuid("439559c2-a3a4-4a25-b4b2-1a0299e287ee"));
			Assertions.assertEquals("ui.i18n.EncounterType.name.439559c2-a3a4-4a25-b4b2-1a0299e287ee",
			    Context.getMessageSourceService()
			            .getMessage("ui.i18n.EncounterType.name.439559c2-a3a4-4a25-b4b2-1a0299e287ee", null, localeEn));
			Assertions.assertEquals("ui.i18n.EncounterType.name.439559c2-a3a4-4a25-b4b2-1a0299e287ee",
			    Context.getMessageSourceService()
			            .getMessage("ui.i18n.EncounterType.name.439559c2-a3a4-4a25-b4b2-1a0299e287ee", null, localeKm));
			
			Assertions.assertEquals("org.openmrs.EncounterType.439559c2-a3a4-4a25-b4b2-1a0299e287ee",
			    Context.getMessageSourceService()
			            .getMessage("org.openmrs.EncounterType.439559c2-a3a4-4a25-b4b2-1a0299e287ee", null, localeEn));
			Assertions.assertEquals("org.openmrs.EncounterType.439559c2-a3a4-4a25-b4b2-1a0299e287ee",
			    Context.getMessageSourceService()
			            .getMessage("org.openmrs.EncounterType.439559c2-a3a4-4a25-b4b2-1a0299e287ee", null, localeKm));
			
			EncounterType et = es.getEncounterType("Triage Encounter");
			String uuid = et.getUuid();
			Assertions.assertEquals("ui.i18n.EncounterType.name." + uuid,
			    Context.getMessageSourceService().getMessage("ui.i18n.EncounterType.name." + uuid, null, localeEn));
			Assertions.assertEquals("ui.i18n.EncounterType.name." + uuid,
			    Context.getMessageSourceService().getMessage("ui.i18n.EncounterType.name." + uuid, null, localeKm));
			Assertions.assertEquals("org.openmrs.EncounterType." + uuid,
			    Context.getMessageSourceService().getMessage("org.openmrs.EncounterType." + uuid, null, localeEn));
			Assertions.assertEquals("org.openmrs.EncounterType." + uuid,
			    Context.getMessageSourceService().getMessage("org.openmrs.EncounterType." + uuid, null, localeKm));
		}
	}
}
