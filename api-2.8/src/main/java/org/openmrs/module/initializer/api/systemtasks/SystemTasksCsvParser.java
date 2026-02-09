/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.systemtasks;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.tasks.SystemTask;
import org.openmrs.module.tasks.api.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * CSV parser for SystemTask entities. Handles bootstrapping (finding or creating) and saving
 * SystemTask instances.
 */
@OpenmrsProfile(modules = { "tasks:*" })
public class SystemTasksCsvParser extends CsvParser<SystemTask, BaseLineProcessor<SystemTask>> {
	
	private TasksService tasksService;
	
	@Autowired
	public SystemTasksCsvParser(@Qualifier("tasks.TasksService") TasksService tasksService,
	    SystemTasksLineProcessor processor) {
		super(processor);
		this.tasksService = tasksService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.SYSTEM_TASKS;
	}
	
	@Override
	public SystemTask bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		if (StringUtils.isEmpty(uuid)) {
			throw new IllegalArgumentException("uuid is required for system tasks");
		}
		
		SystemTask systemTask = tasksService.getSystemTaskByUuid(uuid);
		if (systemTask == null) {
			systemTask = new SystemTask();
			systemTask.setUuid(uuid);
		}
		return systemTask;
	}
	
	@Override
	public SystemTask save(SystemTask instance) {
		return tasksService.saveSystemTask(instance);
	}
}
