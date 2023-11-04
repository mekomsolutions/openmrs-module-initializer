package org.openmrs.module.initializer.api.loaders;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.util.DatabaseUpdater;

import java.io.File;

@OpenmrsProfile(openmrsPlatformVersion = "2.1.1 - 2.4.*")
public class LiquibaseLoader extends BaseFileLoader {
	
	public static final String LIQUIBASE_FILE_NAME = "liquibase";
	
	@Override
	protected Domain getDomain() {
		return Domain.LIQUIBASE;
	}
	
	@Override
	protected String getFileExtension() {
		return "xml";
	}
	
	@Override
	protected void load(File file) throws Exception {
		if (file.getName().equalsIgnoreCase(LIQUIBASE_FILE_NAME + "." + getFileExtension())) {
			DatabaseUpdater.executeChangelog(file.getPath(), null);
		}
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
}
