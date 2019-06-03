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
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleConstants;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyConstants;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.exti18n.ExtI18nConstants;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsConstants;

@Ignore
public class AddressHierarchyMessagesLoadingTest extends DomainBaseModuleContextSensitiveTest {
	
	protected static final String MODULES_TO_LOAD = "org/openmrs/module/addresshierarchy/include/"
	        + ExtI18nConstants.MODULE_ARTIFACT_ID + ".omod";
	
	private InitializerMessageSource inizSrc;
	
	@Before
	public void setup() {
		// Disabling AH full caching otherwise loading takes too long
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(
		        AddressHierarchyConstants.GLOBAL_PROP_INITIALIZE_ADDRESS_HIERARCHY_CACHE_ON_STARTUP, "false"));
		
		Context.getAdministrationService()
		        .saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "en, km_KH"));
		
		Context.getAdministrationService()
		        .saveGlobalProperty(new GlobalProperty(ExtI18nConstants.GLOBAL_PROP_REV_I18N_SUPPORT, "true"));
		runtimeProperties.setProperty(ModuleConstants.RUNTIMEPROPERTY_MODULE_LIST_TO_LOAD, MODULES_TO_LOAD);
		ModuleUtil.startup(runtimeProperties);
		Assert.assertTrue(ModuleFactory.isModuleStarted(ExtI18nConstants.MODULE_ARTIFACT_ID));
		
		inizSrc = (InitializerMessageSource) Context.getMessageSourceService().getActiveMessageSource();
	}
	
	@After
	public void tearDown() {
		Context.getAdministrationService()
		        .saveGlobalProperty(new GlobalProperty(ExtI18nConstants.GLOBAL_PROP_REV_I18N_SUPPORT, "false"));
		ModuleFactory.stopModule(ModuleFactory.getModuleById(ExtI18nConstants.MODULE_ARTIFACT_ID));
	}
	
	@Test
	@Verifies(value = "should load i18n messages specific to the address hierarchy configuration", method = "refreshCache()")
	public void refreshCache_shouldLoadAddressHierarchyMessages() throws IOException {
		
		// Replay
		inizSrc.refreshCache();
		AddressConfigurationLoader.loadAddressConfiguration();
		
		AddressHierarchyService ahs = Context.getService(AddressHierarchyService.class);
		ahs.initI18nCache();
		InitializerService iniz = Context.getService(InitializerService.class);
		
		File csvFile = (new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), iniz.getRejectionsDirPath(),
		        InitializerConstants.DOMAIN_ADDR)).getConfigFile("addresshierarchy.csv");
		LineNumberReader lnr = new LineNumberReader(new FileReader(csvFile));
		lnr.skip(Long.MAX_VALUE);
		int csvLineCount = lnr.getLineNumber() + 1;
		lnr.close();
		Assert.assertTrue(csvLineCount < ahs.getAddressHierarchyEntryCount()); // there should be more entries than the
		                                                                       // number of lines in CSV import
		
		// Working in km_KH
		Context.getUserContext().setLocale(new Locale("km", "KH"));
		PersonAddress address = new PersonAddress();
		address.setStateProvince("កំពង់ស្ពឺ");
		address.setCountyDistrict("ច្បារមន");
		address.setAddress1("សុព័រទេព");
		
		// Looking for possible villages based on an address provided in km_KH
		AddressHierarchyLevel villageLevel = ahs.getAddressHierarchyLevelByAddressField(AddressField.CITY_VILLAGE);
		List<AddressHierarchyEntry> villageEntries = ahs.getPossibleAddressHierarchyEntries(address, villageLevel);
		Assert.assertFalse(CollectionUtils.isEmpty(villageEntries));
		
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
