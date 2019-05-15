package org.openmrs.module.initializer.api.utils;

import org.openmrs.Role;
import org.openmrs.api.UserService;

public class RoleListParser extends ListParser<Role> {
	
	private UserService us;
	
	public RoleListParser(UserService us) {
		this.us = us;
	}
	
	@Override
	protected Role fetch(String id) {
		return Utils.fetchRole(id, us);
	}
	
}
