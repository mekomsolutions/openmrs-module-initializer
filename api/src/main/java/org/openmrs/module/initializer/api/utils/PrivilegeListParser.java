package org.openmrs.module.initializer.api.utils;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;

public class PrivilegeListParser extends ListParser<Privilege> {
	
	private UserService us;
	
	public PrivilegeListParser(UserService us) {
		this.us = us;
	}
	
	@Override
	protected Privilege fetch(String id) {
		return Utils.fetchPrivilege(id, us);
	}
	
}
