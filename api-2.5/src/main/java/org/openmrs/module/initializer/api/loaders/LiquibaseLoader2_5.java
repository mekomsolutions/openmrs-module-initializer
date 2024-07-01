package org.openmrs.module.initializer.api.loaders;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.liquibase.ChangeSetExecutorCallback;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.util.DatabaseUpdater;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@OpenmrsProfile(openmrsPlatformVersion = "2.5.5")
public class LiquibaseLoader2_5 extends BaseFileLoader {
	
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
			// Because liquibase uses the path provided as an identifier of the changeset, we need to relativize it
			// This ensures that liquibase changesets are not re-executed if the absolute path on the server changes
			String absolutePath = file.getAbsolutePath();
			String relativePath = getPathRelativeToApplicationDataDirectory(file);
			updateExistingLiquibaseChangeLogPathsIfNeeded(absolutePath, relativePath);
			try {
				DatabaseUpdater.executeChangelog(relativePath, (ChangeSetExecutorCallback) null);
			}
			catch (Exception e) {
				log.error("An error occurred executing liquibase file: " + file, e);
				throw e;
			}
		}
	}
	
	/**
	 * If a particular changeset file is being executed, and this is referenced by the given oldPath,
	 * then update that changeset to reference the given newPath
	 */
	protected void updateExistingLiquibaseChangeLogPathsIfNeeded(String oldPath, String newPath) {
		log.debug("Checking if liquibase filenames need to be updated for " + oldPath);
		int numRows = sqlCount("select count(*) from liquibasechangelog where filename = '" + oldPath + "'");
		if (numRows > 0) {
			log.warn("Liquibase filename update is needed for " + oldPath + ". Updating to " + newPath);
			sqlUpdate("update liquibasechangelog set filename = '" + newPath + "' where filename = '" + oldPath + "'");
		} else {
			log.debug("Liquibase filename update is not required");
		}
	}
	
	/**
	 * @param file the file to relativize
	 * @return the path of the given file, relativized to the OpenMRS application data directory
	 */
	protected String getPathRelativeToApplicationDataDirectory(File file) {
		Path appDataDirPath = Paths.get(OpenmrsUtil.getApplicationDataDirectory());
		Path liquibaseFilePath = file.toPath();
		return appDataDirPath.relativize(liquibaseFilePath).toString();
	}
	
	/**
	 * @param sql the sql to execute
	 * @return the results of that execution, where the first column of the first row is returned as an
	 *         integer count
	 */
	private int sqlCount(String sql) {
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
		Object singleResult = results.get(0).get(0);
		return Integer.parseInt(singleResult.toString());
	}
	
	/**
	 * @param sql the sql to execute as a database update operation
	 */
	private void sqlUpdate(String sql) {
		Context.getAdministrationService().executeSQL(sql, false);
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
}
