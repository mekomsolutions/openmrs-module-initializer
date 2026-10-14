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

import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesLoader;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

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
	
	@BeforeEach
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
	public void load_shouldModifyExistingIdentifierSources() throws Exception {
		
		// Replay
		loader.loadUnsafe(null, true);
		
		// Verify that existing sources are appropriately edited
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(EXISTING_SEQ);
			SequentialIdentifierGenerator generator = (SequentialIdentifierGenerator) source;
			Assertions.assertEquals("Edited sequential name", generator.getName());
			Assertions.assertEquals("Edited sequential description", generator.getDescription());
			Assertions.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", generator.getIdentifierType().getName());
			Assertions.assertEquals("ACDEFGHJKLMNPRTUVWXY1234567890", generator.getBaseCharacterSet());
			Assertions.assertEquals(6, generator.getMinLength().intValue());
			Assertions.assertEquals(6, generator.getMaxLength().intValue());
			Assertions.assertEquals("Y", generator.getPrefix());
			// the idgen service is advised by core's RequiredDataAdvice, which saves empty strings as null
			Assertions.assertNull(generator.getSuffix());
			Assertions.assertEquals("1000", generator.getFirstIdentifierBase());
			Assertions.assertFalse(BooleanUtils.isTrue(generator.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(EXISTING_REMOTE);
			RemoteIdentifierSource remoteSource = (RemoteIdentifierSource) source;
			Assertions.assertEquals("Edited remote name", remoteSource.getName());
			Assertions.assertEquals("Edited remote description", remoteSource.getDescription());
			Assertions.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", remoteSource.getIdentifierType().getName());
			Assertions.assertEquals("http://example.com/edit", remoteSource.getUrl());
			Assertions.assertEquals("editUser", remoteSource.getUser());
			Assertions.assertEquals("editPass", remoteSource.getPassword());
			Assertions.assertFalse(BooleanUtils.isTrue(remoteSource.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(EXISTING_POOL);
			IdentifierPool pool = (IdentifierPool) source;
			Assertions.assertEquals("Edited pool name", pool.getName());
			Assertions.assertEquals("Edited pool description", pool.getDescription());
			Assertions.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", pool.getIdentifierType().getName());
			Assertions.assertEquals(10, pool.getBatchSize().intValue());
			Assertions.assertEquals(40, pool.getMinPoolSize().intValue());
			Assertions.assertFalse(pool.getRefillWithScheduledTask());
			Assertions.assertFalse(pool.getSequential());
			Assertions.assertFalse(BooleanUtils.isTrue(pool.getRetired()));
		}
	}
	
	@Test
	public void load_shouldCreateNewIdentifierSources() throws Exception {
		
		// Replay
		loader.loadUnsafe(null, true);
		
		// Verify that existing sources are appropriately edited
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(NEW_SEQ);
			SequentialIdentifierGenerator generator = (SequentialIdentifierGenerator) source;
			Assertions.assertEquals("New sequential name", generator.getName());
			Assertions.assertEquals("New sequential description", generator.getDescription());
			Assertions.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", generator.getIdentifierType().getName());
			Assertions.assertEquals("0123456789", generator.getBaseCharacterSet());
			Assertions.assertEquals(5, generator.getMinLength().intValue());
			Assertions.assertEquals(7, generator.getMaxLength().intValue());
			Assertions.assertEquals("A", generator.getPrefix());
			Assertions.assertEquals("Z", generator.getSuffix());
			Assertions.assertEquals("001", generator.getFirstIdentifierBase());
			Assertions.assertFalse(BooleanUtils.isTrue(generator.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(NEW_REMOTE);
			RemoteIdentifierSource remoteSource = (RemoteIdentifierSource) source;
			Assertions.assertEquals("New remote name", remoteSource.getName());
			Assertions.assertEquals("New remote description", remoteSource.getDescription());
			Assertions.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", remoteSource.getIdentifierType().getName());
			Assertions.assertEquals("http://localhost", remoteSource.getUrl());
			Assertions.assertEquals("value-from-runtime-property", remoteSource.getUser());
			Assertions.assertEquals("value-from-system-property", remoteSource.getPassword());
			Assertions.assertFalse(BooleanUtils.isTrue(remoteSource.getRetired()));
		}
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid(NEW_POOL);
			IdentifierPool pool = (IdentifierPool) source;
			Assertions.assertEquals("New pool name", pool.getName());
			Assertions.assertEquals("New pool description", pool.getDescription());
			Assertions.assertEquals("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID", pool.getIdentifierType().getName());
			Assertions.assertEquals(NEW_SEQ, pool.getSource().getUuid());
			Assertions.assertEquals(20, pool.getBatchSize().intValue());
			Assertions.assertEquals(60, pool.getMinPoolSize().intValue());
			Assertions.assertTrue(pool.getRefillWithScheduledTask());
			Assertions.assertTrue(pool.getSequential());
			Assertions.assertFalse(BooleanUtils.isTrue(pool.getRetired()));
		}
	}
	
	@BeforeEach
	@Override
	public void setupAppDataDir() {
		
		String path = getAppDataDirPath();
		
		System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
		System.setProperty("idgen_remote_password", "value-from-system-property");
		Properties prop = new Properties();
		prop.setProperty("idgen_remote_user", "value-from-runtime-property");
		prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
		Context.setRuntimeProperties(prop);
	}
}
