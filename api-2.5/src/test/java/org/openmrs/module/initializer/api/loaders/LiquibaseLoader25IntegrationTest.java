/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.loaders;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class LiquibaseLoader25IntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private LiquibaseLoader2_5 loader;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService adminService;
	
	@Before
	public void setup() throws Exception {
		System.setProperty("useInMemoryDatabase", "true");
		ClassLoader cl = OpenmrsClassLoader.getInstance();
		String schemaSql = IOUtils.resourceToString("liquibase-schema.sql", StandardCharsets.UTF_8, cl);
		adminService.executeSQL(schemaSql, false);
	}
	
	@After
	public void teardown() {
		adminService.executeSQL("drop table LIQUIBASECHANGELOG", false);
		adminService.executeSQL("drop table LIQUIBASECHANGELOGLOCK", false);
	}
	
	@Test
	public void load_shouldExecuteNewChangeSet() {
		String relativePath = "configuration/liquibase/liquibase.xml";
		String absolutePath = getAppDataDirPath() + relativePath;
		Assert.assertEquals(0, numChangeLogEntries(absolutePath));
		Assert.assertEquals(0, numChangeLogEntries(relativePath));
		Assert.assertNull(adminService.getGlobalProperty("test_changes_1"));
		Assert.assertNull(adminService.getGlobalProperty("test_changes_2"));
		loader.load();
		Assert.assertEquals(0, numChangeLogEntries(absolutePath));
		Assert.assertEquals(2, numChangeLogEntries(relativePath));
		Assert.assertEquals("true", adminService.getGlobalProperty("test_changes_1"));
		Assert.assertEquals("true", adminService.getGlobalProperty("test_changes_2"));
	}
	
	@Test
	public void load_shouldUpdateAbsolutePathToRelativePathIfNeeded() {
		String relativePath = "configuration/liquibase/liquibase.xml";
		String absolutePath = getAppDataDirPath() + relativePath;
		insertExistingChangeLogEntry(absolutePath);
		Assert.assertEquals(1, numChangeLogEntries(absolutePath));
		Assert.assertEquals(0, numChangeLogEntries(relativePath));
		loader.updateExistingLiquibaseChangeLogPathsIfNeeded(absolutePath, relativePath);
		Assert.assertEquals(0, numChangeLogEntries(absolutePath));
		Assert.assertEquals(1, numChangeLogEntries(relativePath));
	}
	
	private int numChangeLogEntries(String filename) {
		List<List<Object>> ret = adminService
		        .executeSQL("select count(*) from liquibasechangelog where filename = '" + filename + "'", true);
		return Integer.parseInt(ret.get(0).get(0).toString());
	}
	
	private void insertExistingChangeLogEntry(String filename) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO LIQUIBASECHANGELOG (");
		sb.append(" ID, ");
		sb.append(" AUTHOR, ");
		sb.append(" FILENAME, ");
		sb.append(" DATEEXECUTED, ");
		sb.append(" ORDEREXECUTED, ");
		sb.append(" EXECTYPE, ");
		sb.append(" MD5SUM, ");
		sb.append(" DESCRIPTION, ");
		sb.append(" COMMENTS, ");
		sb.append(" LIQUIBASE ");
		sb.append(") ");
		sb.append("values (");
		sb.append(" 'previousSqlFileChangeset', ");
		sb.append(" 'iniz', ");
		sb.append(" '").append(filename).append("', ");
		sb.append(" '2023-11-01 00:00:00', ");
		sb.append(" 1, ");
		sb.append(" 'EXECUTED', ");
		sb.append(" '8:4019e34234869ff55c81acf9d779f2a7', ");
		sb.append(" 'sqlFile', ");
		sb.append(" '', ");
		sb.append(" '4.4.1'");
		sb.append(")");
		adminService.executeSQL(sb.toString(), false);
	}
}
