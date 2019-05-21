package org.openmrs.module.initializer.api.utils;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeListParser extends ListParser<Privilege> {
	
	@Autowired
	@Qualifier("userService")
	private UserService us;
	
	protected PrivilegeListParser() {
	}
	
	public PrivilegeListParser(UserService us) {
		this.us = us;
	}
	
	@Override
	protected Privilege fetch(String id) {
		return Utils.fetchPrivilege(id, us);
	}
	
}
