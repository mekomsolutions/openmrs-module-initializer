package org.openmrs.module.initializer.liquibase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;

public class DeleteDomainChecksumsChangesetIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static String LIQUIBASE_FILE = "liquibase.xml";
	
	@Autowired
	private ConceptsLoader loader;
	
	@Autowired
	private InitializerService service;
	
	@BeforeEach
	public void setup() throws Exception {
		loader.load();
	}
	
	@AfterEach
	public void deleteCommittedData() {
		// The changeset commits the test's transaction, so without this the concepts loaded in setup would outlive the
		// rollback and collide with the datasets of the test classes that run after this one
		deleteAllData();
	}
	
	@Test
	public void shouldSuccessfullyDeleteSpecifiedDomainChecksums() throws Exception {
		// setup
		File conceptsChecksumsDir = new File(service.getChecksumsDirPath() + File.separator + Domain.CONCEPTS.getName());
		assertFalse(Arrays.asList(conceptsChecksumsDir.list()).isEmpty());
		
		//replay
		runLiquibaseChangeset(LIQUIBASE_FILE);
		
		// verify
		assertTrue(Arrays.asList(conceptsChecksumsDir.list()).isEmpty());
	}
	
	private void runLiquibaseChangeset(String filename) throws Exception {
		Liquibase liquibase = getLiquibase(filename);
		liquibase.update("Deleting 'concepts' domain checksums");
		liquibase.getDatabase().getConnection().commit();
	}
	
	private Liquibase getLiquibase(String filename) throws Exception {
		Database liquibaseConnection = DatabaseFactory.getInstance()
		        .findCorrectDatabaseImplementation(new JdbcConnection(getConnection()));
		
		liquibaseConnection.setDatabaseChangeLogTableName("LIQUIBASECHANGELOG_1");
		liquibaseConnection.setDatabaseChangeLogLockTableName("LIQUIBASECHANGELOGLOCK_1");
		
		// Liquibase 4 rejects a changelog path found more than once on the classpath, and every module API jar on the
		// test classpath ships a liquibase.xml, so only Initializer's own resources directory is made accessible
		File resourcesDir = new File(getClass().getClassLoader().getResource(filename).toURI()).getParentFile();
		return new Liquibase(filename, new DirectoryResourceAccessor(resourcesDir), liquibaseConnection);
	}
}
