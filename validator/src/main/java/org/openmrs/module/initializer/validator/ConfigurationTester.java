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
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
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
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.openconceptlab.OpenConceptLabActivator;
import org.openmrs.test.TestUtil;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

public class ConfigurationTester extends DomainBaseModuleContextSensitiveTest {
	
	private String configDirPath;
	
	private String cielFilePath;
	
	protected String getAppDataDirPath() {
		return Paths.get(configDirPath).getParent().toString();
	}
	
	protected void setMariaDB4jProps(Properties props) throws ManagedProcessException, URISyntaxException {
		DB db = DB.newEmbeddedDB(DBConfigurationBuilder.newBuilder().build());
		db.start();
		db.createDB("openmrs");
		
		//		props.setProperty(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
		props.setProperty(Environment.DIALECT, MySQLDialect.class.getName());
		
		String url = db.getConfiguration().getURL("openmrs");
		URIBuilder ub = new URIBuilder(StringUtils.substringAfter(url, "jdbc:"));
		ub.setParameter("serverTimezone", "UTC");
		url = "jdbc:" + ub.build().toString();
		props.setProperty(Environment.URL, url);
		
		props.setProperty(Environment.USER, "root");
		props.setProperty(Environment.PASS, "");
		
		// automatically create the tables defined in the hbm files
		props.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
	}
	
	@Override
	public Properties getRuntimeProperties() {
		
		if (runtimeProperties == null) {
			runtimeProperties = TestUtil.getRuntimeProperties(getWebappName());
		}
		
		try {
			setMariaDB4jProps(runtimeProperties);
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
	public void setAutoIncrementOnTablesWithNativeIfNotAssignedIdentityGenerator() {
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
	
	public ConfigurationTester() {
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
		
	}
	
	@Before
	public void prepare() throws Exception {
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
		// The OCL domains needs a DaemonToken so here we provide one if it's loaded
		List<InitializerConfig> configs = Context.getRegisteredComponents(InitializerConfig.class);
		
		if (configs != null && configs.size() > 1) {
			final InitializerConfig config = configs.get(0);
			final boolean domainSpecified = config.getFilteredDomains().contains("ocl");
			final boolean includeSpecifiedDomains = config.isInclusionList();
			
			if ((includeSpecifiedDomains && domainSpecified) ||
					(!includeSpecifiedDomains && !domainSpecified)) {
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
		}
	}
	
	@Test
	public void loadConfiguration() throws Exception {
		getService().loadUnsafe(true, Validator.unsafe);
	}
	
	@After
	public void conclude() throws URISyntaxException {
		StringBuilder sb = new StringBuilder();
		sb.append("The validation was not successful and finished with errors.\n");
		if (Validator.getLogFilePath() != null) {
			sb.append("Please check the warnings and errors logged at " + Validator.getLogFilePath());
		}
		Assert.assertThat(sb.toString(), Validator.errors, is(empty()));
	}
}
