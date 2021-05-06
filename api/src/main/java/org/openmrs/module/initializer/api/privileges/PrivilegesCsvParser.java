package org.openmrs.module.initializer.api.privileges;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesCsvParser extends CsvParser<Privilege, BaseLineProcessor<Privilege>> {
	
	private UserService userService;
	
	@Autowired
	public PrivilegesCsvParser(@Qualifier("userService") UserService userService, PrivilegeLineProcessor processor) {
		super(processor);
		this.userService = userService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PRIVILEGES;
	}
	
	@Override
	public Privilege bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		String privilegeName = line.get(PrivilegeLineProcessor.HEADER_PRIVILEGE_NAME, true);
		
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
	
	/**
	 * @see CsvParser#shouldFillInstance(BaseOpenmrsObject, CsvLine) Since privilege does not contain a
	 *      primary key id, override default behavior
	 */
	@Override
	protected boolean shouldFillInstance(Privilege instance, CsvLine csvLine) {
		boolean isVoidedOrRetired = BaseLineProcessor.getVoidOrRetire(csvLine);
		if (!isVoidedOrRetired) {
			return true;
		}
		Privilege existingPrivilege = userService.getPrivilegeByUuid(csvLine.getUuid());
		if (existingPrivilege == null) {
			String privilegeName = csvLine.get(PrivilegeLineProcessor.HEADER_PRIVILEGE_NAME, true);
			existingPrivilege = userService.getPrivilege(privilegeName);
		}
		return existingPrivilege == null;
	}
	
	@Override
	public Privilege save(Privilege instance) {
		return userService.savePrivilege(instance);
	}
}
