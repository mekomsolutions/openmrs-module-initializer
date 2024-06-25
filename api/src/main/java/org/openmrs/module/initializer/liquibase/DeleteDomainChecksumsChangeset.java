package org.openmrs.module.initializer.liquibase;

import static org.openmrs.module.initializer.InitializerConstants.DIR_NAME_CONFIG;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * This change set is used to delete initializer domain checksums which in turn triggers a reload of
 * the domain.
 */
public class DeleteDomainChecksumsChangeset implements CustomTaskChange {
	
	private static final Logger log = LoggerFactory.getLogger(DeleteDomainChecksumsChangeset.class);
	
	private String domainName;
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	/**
	 * @see CustomTaskChange#execute(Database)
	 */
	@Override
	public void execute(Database database) {
		Domain domain;
		try {
			domain = Domain.valueOf(getDomainName().toUpperCase());
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("'" + getDomainName() + "' is not a valid domain name.", e);
		}
		String checksumsDirPath = Paths
		        .get(OpenmrsUtil.getApplicationDataDirectory(), InitializerConstants.DIR_NAME_CHECKSUM).toString();
		
		String configDirPath = getBasePath().resolve(DIR_NAME_CONFIG).toString();
		ConfigDirUtil util = new ConfigDirUtil(configDirPath, checksumsDirPath, domain.getName());
		util.deleteChecksums();
	}
	
	/**
	 * @see liquibase.change.custom.CustomChange#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage() {
		return "Finished deleting all checksums files for the " + getDomainName() + " domain.";
	}
	
	/**
	 * @see liquibase.change.custom.CustomChange#setUp()
	 */
	@Override
	public void setUp() throws SetupException {
	}
	
	/**
	 * @see liquibase.change.custom.CustomChange#setFileOpener(ResourceAccessor)
	 */
	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
	}
	
	/**
	 * @see liquibase.change.custom.CustomChange#validate(Database)
	 */
	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}
	
	private Path getBasePath() {
		return Paths.get(new File(OpenmrsUtil.getApplicationDataDirectory()).toURI());
	}
}
