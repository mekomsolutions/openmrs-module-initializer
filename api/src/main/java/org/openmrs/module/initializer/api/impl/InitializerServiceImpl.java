/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigLoaderUtil;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesConfig;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.util.OpenmrsUtil;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public String getConfigPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.CONFIG_PATH).toString();
	}
	
	@Override
	public String getAddressHierarchyConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_ADDR)
		        .toString();
	}
	
	@Override
	public String getGlobalPropertiesConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_GP)
		        .toString();
	}
	
	@Override
	public void loadGlobalProperties() {
		
		final ConfigLoaderUtil util = new ConfigLoaderUtil(getGlobalPropertiesConfigPath()); // a config. loader util for the target dir
		
		final List<GlobalProperty> globalProperties = new ArrayList<GlobalProperty>();
		for (File file : util.getFiles("xml")) { // processing all the XML files inside the domain
		
			String fileRelPath = util.getRelativePath(file.getPath());
			String checksum = util.getChecksumIfChanged(fileRelPath);
			if (checksum.isEmpty()) {
				continue;
			}
			
			GlobalPropertiesConfig config = new GlobalPropertiesConfig();
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				config = InitializerSerializer.getGlobalPropertiesConfig(is);
				globalProperties.addAll(config.getGlobalProperties());
				util.writeChecksum(fileRelPath, checksum); // the updated config. file is marked as processed
				log.info("The global properties config. file has been processed: " + fileRelPath);
			}
			catch (Exception e) {
				log.error("Could not load the global properties from file: " + file.getPath());
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
		
		log.info("Saving the global properties.");
		Context.getAdministrationService().saveGlobalProperties(globalProperties);
	}
	
	@Override
	public String getMetadataSharingConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_MDS)
		        .toString();
	}
	
	@Override
	public void importMetadataSharingPackages() {
		
		final ConfigLoaderUtil util = new ConfigLoaderUtil(getMetadataSharingConfigPath()); // a config. loader util for the target dir
		
		final PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		ImportConfig importConfig = new ImportConfig();
		importConfig.setPossibleMatch(ImportType.PREFER_THEIRS);
		importConfig.setExactMatch(ImportType.PREFER_THEIRS);
		importer.setImportConfig(importConfig);
		for (File file : util.getFiles("zip")) { // processing all the zip files inside the domain
		
			String fileRelPath = util.getRelativePath(file.getPath());
			String checksum = util.getChecksumIfChanged(fileRelPath);
			if (checksum.isEmpty()) {
				continue;
			}
			
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				importer.loadSerializedPackageStream(is);
				is.close();
				importer.importPackage();
				util.writeChecksum(fileRelPath, checksum); // the updated config. file is marked as processed
				log.info("The following MDS package was succesfully imported: " + fileRelPath);
			}
			catch (Exception e) {
				log.error("The MDS package could not be imported: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
