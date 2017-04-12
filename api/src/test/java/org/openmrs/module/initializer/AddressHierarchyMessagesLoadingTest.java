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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyConstants;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader;
import org.openmrs.module.addresshierarchy.config.ConfigLoaderUtil;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsConstants;

public class AddressHierarchyMessagesLoadingTest extends BaseModuleContextSensitiveTest {
	
	protected static final Log log = LogFactory.getLog(AddressHierarchyMessagesLoadingTest.class);
	
	private static String APP_DATA_TEST_DIRECTORY = "testAppDataDir";
	
	@Before
	public void setup() {
		
		// Diabling AH full caching otherwise loading takes too long
		Context.getAdministrationService()
		        .saveGlobalProperty(
		            new GlobalProperty(AddressHierarchyConstants.GLOBAL_PROP_INITIALIZE_ADDRESS_HIERARCHY_CACHE_ON_STARTUP,
		                    "false"));
		
		String path = getClass().getClassLoader().getResource(APP_DATA_TEST_DIRECTORY).getPath() + File.separator;
		OpenmrsConstants.APPLICATION_DATA_DIRECTORY = path; // The 1.10 way
		Properties prop = new Properties();
		prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
		Context.setRuntimeProperties(prop);
		
		// Enabling i18n suppory on Address Hierarchy
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(AddressHierarchyConstants.GLOBAL_PROP_I18N_SUPPORT, "true"));
		
		ConfigLoaderUtil.deleteChecksums(AddressConfigurationLoader.getSubdirConfigPath());
		AddressConfigurationLoader.loadAddressConfiguration();
	}
	
	@Test
	@Verifies(value = "should load i18n messages specific to the address hierarchy configuration", method = "refreshCache()")
	public void refreshCache_shouldLoadAddressHierarchyMessages() throws IOException {
		
		AddressHierarchyService ahs = Context.getService(AddressHierarchyService.class);
		InitializerService inits = Context.getService(InitializerService.class);
		
		String csvFilePath = new StringBuilder(inits.getAddressHierarchyConfigPath()).append(File.separator)
		        .append("addresshierarchy.csv").toString();
		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(csvFilePath)));
		lnr.skip(Long.MAX_VALUE);
		int csvLineCount = lnr.getLineNumber() + 1;
		lnr.close();
		Assert.assertTrue(csvLineCount < ahs.getAddressHierarchyEntryCount()); // there should be more entries than the number of lines in CSV import
		
		// Working in km_KH
		Context.getUserContext().setLocale(new Locale("km", "KH"));
		PersonAddress address = new PersonAddress();
		address.setStateProvince("កំពង់ស្ពឺ");
		address.setCountyDistrict("ច្បារមន");
		address.setAddress1("សុព័រទេព");
		
		// Looking for possible villages based on an address provided in km_KH
		AddressHierarchyLevel villageLevel = ahs.getAddressHierarchyLevelByAddressField(AddressField.CITY_VILLAGE);
		List<AddressHierarchyEntry> villageEntries = ahs.getPossibleAddressHierarchyEntries(address, villageLevel);
		
		// Verifying that possible villages are provided as i18n message codes
		final Set<String> expectedVillageNames = new HashSet<String>(); // filled by looking at the test CSV
		expectedVillageNames.add("addresshierarchy.tangTonle");
		expectedVillageNames.add("addresshierarchy.rumloung");
		expectedVillageNames.add("addresshierarchy.thlokChheuTeal");
		expectedVillageNames.add("addresshierarchy.trachChrum");
		expectedVillageNames.add("addresshierarchy.paelHael");
		expectedVillageNames.add("addresshierarchy.krangPhka");
		expectedVillageNames.add("addresshierarchy.runloungPrakhleah");
		expectedVillageNames.add("addresshierarchy.preyKanteach");
		expectedVillageNames.add("addresshierarchy.snaoTiPir");
		expectedVillageNames.add("addresshierarchy.roleangSangkae");
		for (AddressHierarchyEntry entry : villageEntries) {
			Assert.assertTrue(expectedVillageNames.contains(entry.getName()));
		}
		
		// Pinpointing a specific village
		address.setCityVillage("ប៉ែលហែល");
		
		// Looking for possible villages
		villageEntries = ahs.getPossibleAddressHierarchyEntries(address, villageLevel);
		
		// We should find our one village
		Assert.assertEquals(1, villageEntries.size());
		String messageKey = villageEntries.get(0).getName();
		Assert.assertEquals(messageKey, "addresshierarchy.paelHael");
		Assert.assertEquals(Context.getMessageSourceService().getMessage(messageKey), "ប៉ែលហែល");
	}
}
