package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.util.DatabaseUpdater;
import org.springframework.stereotype.Component;

@Component
public class LiquibaseLoader extends BaseFileLoader {
	
	private static final String LIQUIBASE_EXTENSION = "xml";
	
	public static final String LIQUIBASE_FILE_NAME = "liquibase." + LIQUIBASE_EXTENSION;
	
	public static final String DATABASE_CHANGELOG_TABLENAME = "liquibasechangelog";
	
	public static final String DATABASE_CHANGELOGLOCK_TABLENAME = "liquibasechangeloglock";
	
	@Override
	protected Domain getDomain() {
		return Domain.LIQUIBASE;
	}
	
	@Override
	protected String getFileExtension() {
		return LIQUIBASE_EXTENSION;
	}
	
	@Override
	protected void load(File file) throws Exception {
		if (file.getName().equalsIgnoreCase(LIQUIBASE_FILE_NAME)) {
			updateDatabase(file.getPath());
		}
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
	
	private void updateDatabase(String filename) throws Exception {
		Liquibase liquibase = getLiquibase(filename);
		liquibase.update(null);
		liquibase.getDatabase().getConnection().commit();
	}
	
	private Liquibase getLiquibase(String filename) throws Exception {
		Connection connection = DatabaseUpdater.getConnection();
		Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
		database.setDatabaseChangeLogTableName(DATABASE_CHANGELOG_TABLENAME);
		database.setDatabaseChangeLogLockTableName(DATABASE_CHANGELOGLOCK_TABLENAME);
		if (connection.getMetaData().getDatabaseProductName().contains("HSQL Database Engine")
				|| connection.getMetaData().getDatabaseProductName().contains("H2")) {
			// a hack because hsqldb and h2 seem to be checking table names in the metadata section case sensitively
			// see https://github.com/openmrs/openmrs-core/blob/e679877103a89937a867d1fdf3aa48f8caaddd73/api/src/main/java/org/openmrs/util/DatabaseUpdater.java#L412-L417
			database.setDatabaseChangeLogTableName(DATABASE_CHANGELOG_TABLENAME.toUpperCase());
			database.setDatabaseChangeLogLockTableName(DATABASE_CHANGELOGLOCK_TABLENAME.toUpperCase());
		}
		
		return new Liquibase(filename, new FileSystemResourceAccessor(), database);
	}
}
