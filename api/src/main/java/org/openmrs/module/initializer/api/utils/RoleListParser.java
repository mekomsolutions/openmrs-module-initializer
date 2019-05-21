package org.openmrs.module.initializer.api.utils;

import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RoleListParser extends ListParser<Role> {
	
	private UserService userService;
	
	@Autowired
	public RoleListParser(@Qualifier("userService") UserService userService) {
		this.userService = userService;
	}
	
	@Override
	protected Role fetch(String id) {
		return Utils.fetchRole(id, userService);
	}
	
}
