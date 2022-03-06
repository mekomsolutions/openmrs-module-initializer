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
			DatabaseUpdater.executeChangelog(file.getAbsolutePath(), null);
		}
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
}
