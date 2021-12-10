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

import org.apache.commons.lang.BooleanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class IdentifierSourcesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private IdentifierSourceService idgenService;
	
	@Autowired
	private IdentifierSourcesLoader loader;
	
	public static final String EXISTING_SEQ = "c1d8a345-3f10-11e4-adec-0800271c1b75";
	
	public static final String EXISTING_REMOTE = "c1d90956-3f10-11e4-adec-0800271c1b75";
	
	public static final String EXISTING_POOL = "ef35fb58-6618-411a-a331-bff960a29d40";
	
	public static final String NEW_SEQ = "1af1422c-8c65-438d-9770-cbb723821bc8";
	
	public static final String NEW_REMOTE = "d2a10e86-59ce-11ec-8885-0242ac110002";
	
	public static final String NEW_POOL = "30799e8f-59cf-11ec-8885-0242ac110002";
	
	@Before
	public void setup() {
		
		PatientIdentifierType type = new PatientIdentifierType();
		type.setName("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID");
		Context.getPatientService().savePatientIdentifierType(type);
		
		SequentialIdentifierGenerator seqSrc = new SequentialIdentifierGenerator();
		{
			seqSrc.setName("Test sequential identifier generator");
			seqSrc.setUuid(EXISTING_SEQ);
			seqSrc.setIdentifierType(type);
			seqSrc.setBaseCharacterSet("ACDEFGHJKLMNPRTUVWXY1234567890");
			seqSrc.setMaxLength(6);
			seqSrc.setMinLength(6);
			seqSrc.setPrefix("Y");
			seqSrc.setFirstIdentifierBase("1000");
			idgenService.saveIdentifierSource(seqSrc);
		}
		
		{
			RemoteIdentifierSource src = new RemoteIdentifierSource();
			src.setName("Test remote identifier source");
			src.setUuid(EXISTING_REMOTE);
			src.setIdentifierType(type);
			src.setUrl("http://example.com");
			src.setUser("testUser");
			src.setPassword("Testing123");
			idgenService.saveIdentifierSource(src);
		}
		
		{
			IdentifierPool src = new IdentifierPool();
			src.setName("Test identifier pool");
			src.setUuid(EXISTING_POOL);
			src.setIdentifierType(type);
			src.setSource(seqSrc);
			src.setBatchSize(500);
			src.setMinPoolSize(100);
			src.setRefillWithScheduledTask(true);
			src.setSequential(true);
			idgenService.saveIdentifierSource(src);
		}
	}
	
	@Test
	public void load_shouldModifyExistingIdentifierSources() {
		
		// Replay
		loader.load();
		
		// Verify that existing sources are appropriately edited
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(EXISTING_SEQ);
			SequentialIdentifierGenerator generator = (SequentialIdentifierGenerator) source;
			Assert.assertEquals("Edited sequential name", generator.getName());
			Assert.assertEquals("Edited sequential description", generator.getDescription());
			Assert.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", generator.getIdentifierType().getName());
			Assert.assertEquals("ACDEFGHJKLMNPRTUVWXY1234567890", generator.getBaseCharacterSet());
			Assert.assertEquals(6, generator.getMinLength().intValue());
			Assert.assertEquals(6, generator.getMaxLength().intValue());
			Assert.assertEquals("Y", generator.getPrefix());
			Assert.assertEquals("", generator.getSuffix());
			Assert.assertEquals("1000", generator.getFirstIdentifierBase());
			Assert.assertFalse(BooleanUtils.isTrue(generator.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(EXISTING_REMOTE);
			RemoteIdentifierSource remoteSource = (RemoteIdentifierSource) source;
			Assert.assertEquals("Edited remote name", remoteSource.getName());
			Assert.assertEquals("Edited remote description", remoteSource.getDescription());
			Assert.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", remoteSource.getIdentifierType().getName());
			Assert.assertEquals("http://example.com/edit", remoteSource.getUrl());
			Assert.assertEquals("editUser", remoteSource.getUser());
			Assert.assertEquals("editPass", remoteSource.getPassword());
			Assert.assertFalse(BooleanUtils.isTrue(remoteSource.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(EXISTING_POOL);
			IdentifierPool pool = (IdentifierPool) source;
			Assert.assertEquals("Edited pool name", pool.getName());
			Assert.assertEquals("Edited pool description", pool.getDescription());
			Assert.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", pool.getIdentifierType().getName());
			Assert.assertEquals(10, pool.getBatchSize().intValue());
			Assert.assertEquals(40, pool.getMinPoolSize().intValue());
			Assert.assertFalse(pool.getRefillWithScheduledTask());
			Assert.assertFalse(pool.getSequential());
			Assert.assertFalse(BooleanUtils.isTrue(pool.getRetired()));
		}
	}
	
	@Test
	public void load_shouldCreateNewIdentifierSources() {
		
		// Replay
		loader.load();
		
		// Verify that existing sources are appropriately edited
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(NEW_SEQ);
			SequentialIdentifierGenerator generator = (SequentialIdentifierGenerator) source;
			Assert.assertEquals("New sequential name", generator.getName());
			Assert.assertEquals("New sequential description", generator.getDescription());
			Assert.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", generator.getIdentifierType().getName());
			Assert.assertEquals("0123456789", generator.getBaseCharacterSet());
			Assert.assertEquals(5, generator.getMinLength().intValue());
			Assert.assertEquals(7, generator.getMaxLength().intValue());
			Assert.assertEquals("A", generator.getPrefix());
			Assert.assertEquals("Z", generator.getSuffix());
			Assert.assertEquals("001", generator.getFirstIdentifierBase());
			Assert.assertFalse(BooleanUtils.isTrue(generator.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(NEW_REMOTE);
			RemoteIdentifierSource remoteSource = (RemoteIdentifierSource) source;
			Assert.assertEquals("New remote name", remoteSource.getName());
			Assert.assertEquals("New remote description", remoteSource.getDescription());
			Assert.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", remoteSource.getIdentifierType().getName());
			Assert.assertEquals("http://localhost", remoteSource.getUrl());
			Assert.assertEquals("user", remoteSource.getUser());
			Assert.assertEquals("pass", remoteSource.getPassword());
			Assert.assertFalse(BooleanUtils.isTrue(remoteSource.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(NEW_POOL);
			IdentifierPool pool = (IdentifierPool) source;
			Assert.assertEquals("New pool name", pool.getName());
			Assert.assertEquals("New pool description", pool.getDescription());
			Assert.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", pool.getIdentifierType().getName());
			Assert.assertEquals(NEW_SEQ, pool.getSource().getUuid());
			Assert.assertEquals(20, pool.getBatchSize().intValue());
			Assert.assertEquals(60, pool.getMinPoolSize().intValue());
			Assert.assertTrue(pool.getRefillWithScheduledTask());
			Assert.assertTrue(pool.getSequential());
			Assert.assertFalse(BooleanUtils.isTrue(pool.getRetired()));
		}
	}
}
