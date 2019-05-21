package org.openmrs.module.initializer.api.privileges;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeLineProcessor extends BaseLineProcessor<Privilege> {
	
	protected static String HEADER_PRIVILEGE_NAME = "privilege name";
	
	private UserService userService;
	
	@Autowired
	public PrivilegeLineProcessor(@Qualifier("userService") UserService userService) {
		this.userService = userService;
	}
	
	@Override
	protected Privilege bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		String privilegeName = line.get(HEADER_PRIVILEGE_NAME, true);
		
		Privilege privilege = userService.getPrivilegeByUuid(uuid);
		if (privilege != null && !privilege.getPrivilege().equals(privilegeName)) {
			throw new IllegalArgumentException("A privilege name cannot be edited.");
		}
		
		if (privilege == null) {
			if (!StringUtils.isEmpty(privilegeName)) {
				privilege = userService.getPrivilege(privilegeName);
			}
		}
		
		if (privilege == null) {
			privilege = new Privilege();
			if (!StringUtils.isEmpty(uuid)) {
				privilege.setUuid(uuid);
			}
		}
		
		return privilege;
	}
	
	@Override
	protected Privilege fill(Privilege privilege, CsvLine line) throws IllegalArgumentException {
		privilege.setPrivilege(line.get(HEADER_PRIVILEGE_NAME, true));
		privilege.setName(privilege.getPrivilege());
		privilege.setDescription(line.get(HEADER_DESC));
		return privilege;
	}
}
