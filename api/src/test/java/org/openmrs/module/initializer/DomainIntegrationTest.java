/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer;

import static org.openmrs.module.initializer.api.ConfigDirUtil.getExtensionFilenameFilter;
import static org.openmrs.module.initializer.api.ConfigDirUtil.getFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.opencsv.CSVReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.CsvLoader;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * This allows to perform context sensitive tests on a specific domain inside the test app data
 * directory.
 */
public abstract class DomainIntegrationTest extends BaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public static final String appDataTestDir = "testAppDataDir";
	
	/**
	 * @return The configuration {@link Loader} for the domain that is being tested, null if this method
	 *         is irrelevant.
	 */
	protected abstract Loader getLoader();
	
	/**
	 * This method need to be override from domain tests which have rejection data
	 * 
	 * @return ArrayList of rejection data expected for each domain
	 */
	protected List<String[]> getRejectionData() {
		return new ArrayList<String[]>();
	};
	
	@Autowired
	private InitializerService iniz;
	
	protected InitializerService getService() {
		return iniz;
	}
	
	/*
	 * pre-Spring loading setup for all integration tests
	 *
	 * We start all the conditional modules here.
	 */
	public DomainIntegrationTest() {
		super();
		ModuleFactory.getStartedModulesMap().put("idgen", new Module("", "idgen", "", "", "", "4.3"));
		ModuleFactory.getStartedModulesMap().put("metadatasharing", new Module("", "metadatasharing", "", "", "", "1.2.2"));
		ModuleFactory.getStartedModulesMap().put("metadatamapping", new Module("", "metadatamapping", "", "", "", "1.3.4"));
	}
	
	@Before
	public void setupAppDataDir() throws IOException {
		
		String path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
		
		OpenmrsConstants.APPLICATION_DATA_DIRECTORY = path; // The 1.10 way
		System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
		Properties prop = new Properties();
		prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
		Context.setRuntimeProperties(prop);
	}
	
	@After
	public void tearDown() {
		
		analyzeRejections();
		
		ConfigDirUtil.deleteFiles(iniz.getChecksumsDirPath(), null);
	}
	
	private void analyzeRejections() {
		// CSV rejections
		if (getLoader() instanceof CsvLoader) {
			String domainPath = Paths.get(iniz.getRejectionsDirPath(), getLoader().getDomainName()).toString();
			List<File> rejectionCsvFiles = getFiles(domainPath, getExtensionFilenameFilter("csv"));
			
			if (!CollectionUtils.isEmpty(rejectionCsvFiles)) {
				for (File rejectionFile : rejectionCsvFiles) {
					CSVReader reader = null;
					try {
						reader = new CSVReader(new FileReader(rejectionFile));
						List<String[]> allRows = reader.readAll();
						assertCsvRejectionLines(getLoader().getDomainName() + rejectionFile.getName(), allRows);
					}
					catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
			
		}
	}
	
	/**
	 * This method will rejection file has expected rejection data.
	 * 
	 * @param filePath The rejection file path.
	 * @param lines The rejection lines in domain rejection file.
	 */
	protected void assertCsvRejectionLines(String filePath, List<String[]> lines) {
		
		int rejectionDataCount = 0;
		
		for (int i = 1; i < lines.size(); i++) {
			Assert.assertArrayEquals(getRejectionData().get(rejectionDataCount), lines.get(i));
			rejectionDataCount = rejectionDataCount + 1;
		}
		
		if (rejectionDataCount < getRejectionData().size() - 1) {
			log.error("rejection file didn't have all expected rejection data in domain" + filePath);
			Assert.fail();
		}
	}
}
