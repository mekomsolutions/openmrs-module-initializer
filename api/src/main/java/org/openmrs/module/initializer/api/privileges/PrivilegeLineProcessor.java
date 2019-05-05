package org.openmrs.module.initializer.api.privileges;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

public class PrivilegeLineProcessor extends BaseLineProcessor<Privilege, UserService> {
	
	protected static String HEADER_PRIVILEGE_NAME = "privilege name";
	
	public PrivilegeLineProcessor(String[] headerLine, UserService us) {
		super(headerLine, us);
	}
	
	@Override
	protected Privilege bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Privilege privilege = service.getPrivilegeByUuid(uuid);
		
		if (privilege != null) {
			service.purgePrivilege(privilege);
		}
		
		String privilegeName = line.get(HEADER_PRIVILEGE_NAME, true);
		
		if (privilegeName == null) {
			throw new IllegalArgumentException(
			        "A privilege must at least be provided a privilege name: '" + line.toString() + "'");
		}
		
		privilege = service.getPrivilege(privilegeName);
		if (privilege == null) {
			privilege = new Privilege();
		}
		if (!StringUtils.isEmpty(uuid)) {
			privilege.setUuid(uuid);
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
