package org.openmrs.module.initializer.api.privileges;

import org.openmrs.Privilege;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeLineProcessor extends BaseLineProcessor<Privilege> {
	
	protected static String HEADER_PRIVILEGE_NAME = "privilege name";
	
	@Override
	public Privilege fill(Privilege privilege, CsvLine line) throws IllegalArgumentException {
		privilege.setPrivilege(line.get(HEADER_PRIVILEGE_NAME, true));
		privilege.setName(privilege.getPrivilege());
		privilege.setDescription(line.get(HEADER_DESC));
		return privilege;
	}
}
