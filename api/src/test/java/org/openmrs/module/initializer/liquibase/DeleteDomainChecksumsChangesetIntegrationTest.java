package org.openmrs.module.initializer.liquibase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class DeleteDomainChecksumsChangesetIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static String LIQUIBASE_FILE = "liquibase.xml";
	
	@Autowired
	private ConceptsLoader loader;
	
	@Autowired
	private InitializerService service;
	
	@Before
	public void setup() throws Exception {
		loader.load();
	}
	
	@Test
	public void shouldSuccessfullyDeleteSpecifiedDomainChecksums() throws Exception {
		// setup
		File conceptDomainChecksumsDir = new File(
		        service.getChecksumsDirPath() + File.separator + Domain.CONCEPTS.getName());
		assertFalse(Arrays.asList(conceptDomainChecksumsDir.list()).isEmpty());
		
		//replay
		runLiquibaseChangeset(LIQUIBASE_FILE);
		
		// verify
		assertTrue(Arrays.asList(conceptDomainChecksumsDir.list()).isEmpty());
	}
	
	private void runLiquibaseChangeset(String filename) throws Exception {
		Liquibase liquibase = getLiquibase(filename);
		liquibase.update("Deleting 'concepts' domain checksums");
		liquibase.getDatabase().getConnection().commit();
	}
	
	private Liquibase getLiquibase(String filename) throws Exception {
		Database liquibaseConnection = DatabaseFactory.getInstance()
		        .findCorrectDatabaseImplementation(new JdbcConnection(getConnection()));
		
		liquibaseConnection.setDatabaseChangeLogTableName("LIQUIBASECHANGELOG");
		liquibaseConnection.setDatabaseChangeLogLockTableName("LIQUIBASECHANGELOGLOCK");
		
		return new Liquibase(filename, new ClassLoaderResourceAccessor(getClass().getClassLoader()), liquibaseConnection);
	}
}
