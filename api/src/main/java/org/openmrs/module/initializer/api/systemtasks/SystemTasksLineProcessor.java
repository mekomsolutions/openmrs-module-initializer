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
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.tasks.Priority;
import org.openmrs.module.tasks.SystemTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Line processor for SystemTask CSV files. Handles field mapping from CSV columns to SystemTask
 * entity properties.
 */
@OpenmrsProfile(modules = { "tasks:*" })
public class SystemTasksLineProcessor extends BaseLineProcessor<SystemTask> {
	
	private static final Logger log = LoggerFactory.getLogger(SystemTasksLineProcessor.class);
	
	public static final String HEADER_TITLE = "title";
	
	public static final String HEADER_PRIORITY = "priority";
	
	public static final String HEADER_DEFAULT_ASSIGNEE_ROLE = "default assignee role";
	
	public static final String HEADER_RATIONALE = "rationale";
	
	public SystemTasksLineProcessor() {
	}
	
	@Override
	public SystemTask fill(SystemTask systemTask, CsvLine line) throws IllegalArgumentException {
		systemTask.setName(line.getName(true)); // Required - uses HEADER_NAME from base class
		systemTask.setTitle(line.get(HEADER_TITLE, true)); // Required
		systemTask.setDescription(line.get(HEADER_DESC)); // Uses HEADER_DESC from base class
		systemTask.setRationale(line.get(HEADER_RATIONALE));
		
		String priorityStr = line.get(HEADER_PRIORITY);
		if (StringUtils.isNotBlank(priorityStr)) {
			try {
				systemTask.setPriority(Priority.valueOf(priorityStr.toUpperCase()));
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
				        "Invalid priority value: " + priorityStr + ". Must be one of: HIGH, MEDIUM, LOW");
			}
		}
		
		String assigneeRole = line.get(HEADER_DEFAULT_ASSIGNEE_ROLE);
		if (StringUtils.isNotBlank(assigneeRole)) {
			Integer roleId = resolveProviderRoleId(assigneeRole);
			systemTask.setDefaultAssigneeProviderRoleId(roleId);
		}
		
		return systemTask;
	}
	
	/**
	 * Resolves a provider role ID from either a UUID or a name. Uses reflection to avoid hard
	 * dependency on providermanagement module.
	 * 
	 * @param roleReference the UUID or name of the provider role
	 * @return the provider role ID, or null if providermanagement is not available
	 * @throws IllegalArgumentException if the provider role cannot be found
	 */
	private Integer resolveProviderRoleId(String roleReference) {
		try {
			// Try to get ProviderManagementService via reflection
			Class<?> serviceClass = Context.loadClass("org.openmrs.module.providermanagement.api.ProviderManagementService");
			Object service = Context.getService(serviceClass);
			
			if (service == null) {
				log.warn("ProviderManagementService not available. Cannot resolve provider role: {}", roleReference);
				return null;
			}
			
			// Check if it looks like a UUID (contains hyphens and is 36 characters)
			if (roleReference.contains("-") && roleReference.length() == 36) {
				java.lang.reflect.Method getByUuid = serviceClass.getMethod("getProviderRoleByUuid", String.class);
				Object role = getByUuid.invoke(service, roleReference);
				if (role != null) {
					java.lang.reflect.Method getId = role.getClass().getMethod("getProviderRoleId");
					return (Integer) getId.invoke(role);
				}
			}
			
			// Try to find by name
			java.lang.reflect.Method getAllRoles = serviceClass.getMethod("getAllProviderRoles", boolean.class);
			@SuppressWarnings("unchecked")
			java.util.List<?> allRoles = (java.util.List<?>) getAllRoles.invoke(service, false);
			
			for (Object role : allRoles) {
				java.lang.reflect.Method getName = role.getClass().getMethod("getName");
				String roleName = (String) getName.invoke(role);
				if (roleName != null && roleName.equalsIgnoreCase(roleReference)) {
					java.lang.reflect.Method getId = role.getClass().getMethod("getProviderRoleId");
					return (Integer) getId.invoke(role);
				}
			}
			
			throw new IllegalArgumentException("Provider role not found: " + roleReference);
		}
		catch (ClassNotFoundException e) {
			log.warn("providermanagement module not available. Cannot resolve provider role: {}", roleReference);
			return null;
		}
		catch (IllegalArgumentException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Error resolving provider role: {}", roleReference, e);
			return null;
		}
	}
}
