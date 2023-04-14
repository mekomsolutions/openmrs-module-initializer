package org.openmrs.module.initializer.validator;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.openmrs.module.initializer.InitializerConstants.ARG_DOMAINS;
import static org.openmrs.module.initializer.InitializerConstants.ARG_EXCLUDE;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_DOMAINS;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_EXCLUDE;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_SKIPCHECKSUMS;
import static org.openmrs.module.initializer.validator.Validator.ARG_CHECKSUMS;
import static org.openmrs.module.initializer.validator.Validator.ARG_CIEL_FILE;
import static org.openmrs.module.initializer.validator.Validator.ARG_CONFIG_DIR;
import static org.openmrs.module.initializer.validator.Validator.cmdLine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import org.springframework.test.annotation.Rollback;
import org.apache.commons.lang.StringUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLDialect;
import org.hsqldb.cmdline.SqlFile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.openconceptlab.OpenConceptLabActivator;
import org.openmrs.test.TestUtil;
import org.openmrs.util.DatabaseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import ch.vorburger.exec.ManagedProcessException;

public class ConfigurationTester extends DomainBaseModuleContextSensitiveTest {
	
	protected static final Logger log = LoggerFactory.getLogger(ConfigurationTester.class);
	
	private static MySQLContainer mysqlContainer = new MySQLContainer("mysql:5.7.31");
	
	private String configDirPath;
	
	private String cielFilePath;
	
	protected String getAppDataDirPath() {
		return Paths.get(configDirPath).getParent().toString();
	}
	
	@BeforeClass
	public static void setupMySqlDb() throws IOException {
		mysqlContainer.withDatabaseName("openmrs");
		mysqlContainer.withUsername("root");
		mysqlContainer.withPassword("");
		mysqlContainer.withCommand("mysqld --character-set-server=utf8 --collation-server=utf8_general_ci");
		mysqlContainer.start();
	}
	
	protected void setupDatabaseProps(Properties props) throws ManagedProcessException, URISyntaxException {
		props.setProperty(Environment.DIALECT, MySQLDialect.class.getName());
		String url = "jdbc:mysql://localhost:DATABASE_PORT/openmrs?autoReconnect=true&sessionVariables=default_storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
		url = url.replaceAll("DATABASE_PORT", String.valueOf(mysqlContainer.getMappedPort(3306)));
		props.setProperty(Environment.URL, url);
		
		props.setProperty(Environment.USER, "root");
		props.setProperty(Environment.PASS, "");
		props.setProperty("initializer.log.enabled", "true");
		// disable hibernate indexing during searches to avoid LazyInitializationExceptions 
		// on indexed entities after test execution
		props.setProperty("hibernate.search.indexing_strategy", "manual");
		
		// automatically create the tables defined in the hbm files
		props.setProperty(Environment.HBM2DDL_AUTO, "update");
	}
	
	@Override
	public Properties getRuntimeProperties() {
		if (runtimeProperties == null) {
			runtimeProperties = TestUtil.getRuntimeProperties(getWebappName());
		}
		
		try {
			setupDatabaseProps(runtimeProperties);
		}
		catch (ManagedProcessException | URISyntaxException e) {
			log.error("mariaDB4j could not be setup properly, reverting to OpenMRS defaults.", e);
			runtimeProperties = super.getRuntimeProperties();
		}
		return runtimeProperties;
	}
	
	@Override
	public void updateSearchIndex() {
		// to prevent Data Filter's 'Illegal Record Access'
	}
	
	@Override
	protected IDatabaseConnection setupDatabaseConnection(Connection connection) throws DatabaseUnitException {
		IDatabaseConnection dbUnitConn = new DatabaseConnection(connection);
		DatabaseConfig config = dbUnitConn.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		return dbUnitConn;
	}
	
	@Override
	protected void turnOnDBConstraints(Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1;");
		ps.execute();
		ps.close();
	}
	
	@Override
	protected void turnOffDBConstraints(Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0;");
		ps.execute();
		ps.close();
	}
	
	@Override
	public void deleteAllData() {
		
	}
	
	@Override
	public void clearSessionAfterEachTest() {
	}
	
	public ConfigurationTester() throws Exception {
		super();
		
		configDirPath = cmdLine.getOptionValue(ARG_CONFIG_DIR);
		if (cmdLine.hasOption(ARG_CIEL_FILE)) {
			cielFilePath = cmdLine.getOptionValue(ARG_CIEL_FILE);
		}
		if (cmdLine.hasOption(ARG_DOMAINS)) {
			getRuntimeProperties().put(PROPS_DOMAINS, cmdLine.getOptionValue(ARG_DOMAINS));
		}
		Stream.of(Domain.values()).forEach(d -> {
			String arg = ARG_EXCLUDE + "." + d.getName();
			if (cmdLine.hasOption(arg)) {
				getRuntimeProperties().put(PROPS_EXCLUDE + "." + d.getName(), cmdLine.getOptionValue(arg));
			}
		});
		if (!cmdLine.hasOption(ARG_CHECKSUMS)) {
			getRuntimeProperties().put(PROPS_SKIPCHECKSUMS, "true");
		}
		// Setting up initial core database
		DatabaseUpdater.executeChangelog("liquibase-schema-only.xml", null);
		DatabaseUpdater.executeChangelog("liquibase-core-data.xml", null);
		DatabaseUpdater.executeChangelog("liquibase-update-to-latest.xml", null);
	}
	
	@Before
	public void prepare() throws Exception {
		Context.checkCoreDataset();
		if (!StringUtils.isEmpty(cielFilePath)) {
			Connection connection = getConnection();
			SqlFile sqlFile = new SqlFile(Validator.trimCielSqlFile(new File(cielFilePath)));
			sqlFile.setConnection(connection);
			turnOffDBConstraints(connection);
			sqlFile.execute();
			turnOnDBConstraints(connection);
		}
		Properties props = Optional.ofNullable(Context.getRuntimeProperties()).orElse(new Properties());
		props.putAll(getRuntimeProperties());
		Context.setRuntimeProperties(props);
	}
	
	@Before
	public void prepareOcl() {
		// The OCL domains needs a DaemonToken so here we provide one
		Map<String, DaemonToken> daemonTokens;
		try {
			Field field = ModuleFactory.class.getDeclaredField("daemonTokens");
			field.setAccessible(true);
			daemonTokens = (Map<String, DaemonToken>) field.get(null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		DaemonToken daemonToken = new DaemonToken("openconceptlab");
		daemonTokens.put(daemonToken.getId(), daemonToken);
		new OpenConceptLabActivator().setDaemonToken(daemonToken);
	}
	
	@Override
	public void baseSetupWithStandardDataAndAuthentication() throws SQLException {
		// Open a session if needed
		if (!Context.isSessionOpen()) {
			Context.openSession();
		}
		authenticate();
		Context.clearSession();
	}
	
	@Test
	@Rollback(false)
	public void loadConfiguration() throws Exception {
		super.getConnection().setAutoCommit(true);
		getService().loadUnsafe(true, Validator.unsafe);
		Context.flushSession();
		Context.clearSession();
		super.getConnection().setAutoCommit(false);
	}
	
	@After
	public void conclude() throws URISyntaxException {
		StringBuilder sb = new StringBuilder();
		sb.append("The validation was not successful and finished with errors.\n");
		if (Validator.getLogFilePath() != null) {
			sb.append("Please check the warnings and errors logged at " + Validator.getLogFilePath());
		}
		Assert.assertThat(sb.toString(), Validator.errors, is(empty()));
		super.getConnection();
	}
}
