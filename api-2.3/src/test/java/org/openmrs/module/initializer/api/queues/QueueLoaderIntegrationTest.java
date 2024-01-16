/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.queues;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_3_Test;
import org.openmrs.module.queue.api.QueueService;
import org.openmrs.module.queue.model.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class QueueLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_3_Test {
	
	@Autowired
	@Qualifier("queue.QueueService")
	private QueueService queueService;
	
	@Autowired
	private QueueLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-queues.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() throws Exception {
		
		// Initial Queue
		{
			Queue queue = queueService.getQueueByUuid("2a0e0eee-6888-11ee-ab8d-0242ac120002").orElse(null);
			Assert.assertNotNull(queue);
			Assert.assertEquals("Initial Queue", queue.getName());
			Assert.assertEquals("", queue.getDescription());
			Assert.assertEquals(2001, queue.getService().getConceptId().intValue());
			Assert.assertEquals(1, queue.getLocation().getLocationId().intValue());
		}
		
		loader.load();
		
		// Revised Queue
		{
			Queue queue = queueService.getQueueByUuid("2a0e0eee-6888-11ee-ab8d-0242ac120002").orElse(null);
			Assert.assertNotNull(queue);
			Assert.assertEquals("Revised Queue", queue.getName());
			Assert.assertEquals("Revised Description", queue.getDescription());
			Assert.assertEquals(2002, queue.getService().getConceptId().intValue());
			Assert.assertEquals(2, queue.getLocation().getLocationId().intValue());
		}
		// New Queue
		{
			Queue queue = queueService.getQueueByUuid("288db1cc-688a-11ee-ab8d-0242ac120002").orElse(null);
			Assert.assertNotNull(queue);
			Assert.assertEquals("New Queue", queue.getName());
			Assert.assertEquals("New Description", queue.getDescription());
			Assert.assertEquals(2001, queue.getService().getConceptId().intValue());
			Assert.assertEquals(3, queue.getLocation().getLocationId().intValue());
		}
		// Queue with statuses
		{
			Queue queue = queueService.getQueueByUuid("4856c1c1-c9b3-4a7e-8669-4220051ab640").orElse(null);
			Assert.assertNotNull(queue);
			Assert.assertEquals("Triage Queue", queue.getName());
			Assert.assertEquals("Queue with custom statuses", queue.getDescription());
			Assert.assertEquals(2001, queue.getService().getConceptId().intValue());
			Assert.assertEquals(2003, queue.getStatusConceptSet().getConceptId().intValue());
			Assert.assertEquals("Xanadu", queue.getLocation().getName());
		}
	}
}
