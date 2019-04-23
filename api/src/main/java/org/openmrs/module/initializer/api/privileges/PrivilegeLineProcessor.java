package org.openmrs.module.initializer.api.privileges;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

public class PrivilegeLineProcessor extends BaseLineProcessor<Privilege, UserService> {
	
	public PrivilegeLineProcessor(String[] headerLine, UserService us) {
		super(headerLine, us);
	}
	
	@Override
	protected Privilege bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Privilege privilege = service.getPrivilegeByUuid(uuid);
		
		if (privilege == null) {
			String privilegeName = line.get(HEADER_NAME, true);
			if (privilegeName != null) {
				privilege = service.getPrivilege(privilegeName);
			}
		}
		if (privilege == null) {
			privilege = new Privilege();
			if (!StringUtils.isEmpty(uuid)) {
				privilege.setUuid(uuid);
			}
		}
		privilege.setRetired(getVoidOrRetire(line.asLine()));
		
		return privilege;
	}
	
	@Override
	protected Privilege fill(Privilege privilege, CsvLine line) throws IllegalArgumentException {
		
		String privilegeName = line.get(HEADER_NAME, true);
		
		if (privilegeName == null) {
			if (privilege.getName() == null) {
				throw new IllegalArgumentException(
				        "A privilege must at least be provided a privilege name: '" + line.toString() + "'");
			}
			privilegeName = privilege.getName();
		}
		if (privilege.getName() != null && !privilege.getName().equals(privilegeName)) {
			throw new IllegalArgumentException("A privilege name cannot be edited: '" + line.toString() + "'");
		}
		
		privilege.setName(privilegeName);
		privilege.setPrivilege(privilegeName);
		
		String privilegeDesc = line.get(HEADER_DESC, true);
		privilege.setDescription(privilegeDesc);
		
		return privilege;
	}
}
