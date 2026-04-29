/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_ROW_CHECKSUMS_ENABLED;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.loc.LocationsLoader;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validates that when initializer.row.checksums.enabled=true, a CSV loader writes a per-row
 * checksum sidecar file for each successfully processed row, and that a subsequent load with no
 * changes finds those hashes (so all rows are short-circuited).
 */
public class LocationsLoaderRowChecksumIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private LocationsLoader loader;
	
	@Autowired
	private InitializerConfig cfg;
	
	@Autowired
	private InitializerService iniz;
	
	@Before
	public void enableRowChecksums() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
		System.setProperty(PROPS_ROW_CHECKSUMS_ENABLED, "true");
		cfg.init();
	}
	
	@After
	public void disableRowChecksums() {
		System.clearProperty(PROPS_ROW_CHECKSUMS_ENABLED);
		cfg.init();
	}
	
	@Test
	public void load_shouldWriteRowChecksumsForProcessedRows() throws Exception {
		// sanity: flag is on
		assertThat(cfg.isRowChecksumsEnabled(), is(true));
		
		// First load: locations.csv contains 7 rows total, 1 of which fails (invalid parent).
		// We expect 6 row hashes to be persisted (the failing row is intentionally excluded so it
		// is retried on the next run).
		loader.load();
		
		File rowsFile = Paths
		        .get(iniz.getChecksumsDirPath(), "locations", "locations." + ConfigDirUtil.ROW_CHECKSUM_FILE_EXT).toFile();
		assertThat("row checksum sidecar file should exist after a load", rowsFile.exists(), is(true));
		
		long hashCount = Files.readAllLines(rowsFile.toPath()).stream().filter(s -> !s.trim().isEmpty()).count();
		assertThat(hashCount, is(6L));
		
		// Second load: every existing row is now in the previous-hash set, so the failing row is
		// the only one to be re-attempted. The sidecar file remains in place.
		ConfigDirUtil configDirUtil = new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), "locations");
		Set<String> previousHashes = configDirUtil
		        .readRowChecksums(Paths.get(iniz.getConfigDirPath(), "locations", "locations.csv").toFile());
		assertThat(previousHashes.size(), is(6));
		
		loader.load();
		
		assertThat(rowsFile.exists(), is(true));
	}
}
