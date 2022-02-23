package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.util.DatabaseUpdater;
import org.springframework.stereotype.Component;

@Component
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
			Context.flushSession();
			Context.clearSession();
		}
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
}
