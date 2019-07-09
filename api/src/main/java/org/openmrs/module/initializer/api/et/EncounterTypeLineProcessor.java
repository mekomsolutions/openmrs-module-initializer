package org.openmrs.module.initializer.api.et;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.EncounterType;
import org.openmrs.Privilege;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EncounterTypeLineProcessor extends BaseLineProcessor<EncounterType> {
	
	protected static String HEADER_VIEW_PRIV = "view privilege";
	
	protected static String HEADER_EDIT_PRIV = "edit privilege";
	
	private EncounterService service;
	
	private UserService userService;
	
	@Autowired
	public EncounterTypeLineProcessor(@Qualifier("encounterService") EncounterService encounterService,
	    @Qualifier("userService") UserService userService) {
		super();
		this.service = encounterService;
		this.userService = userService;
	}
	
	@Override
	protected EncounterType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		EncounterType type = service.getEncounterTypeByUuid(uuid);
		if (type == null) {
			type = service.getEncounterType(line.get(HEADER_NAME));
		}
		if (type == null) {
			type = new EncounterType();
			if (!StringUtils.isEmpty(uuid)) {
				type.setUuid(uuid);
			}
		}
		
		return type;
	}
	
	protected EncounterType fill(EncounterType type, CsvLine line) throws IllegalArgumentException {
		
		type.setName(line.get(HEADER_NAME, true));
		type.setDescription(line.get(HEADER_DESC));
		{
			String privilegeId = line.get(HEADER_VIEW_PRIV);
			if (!StringUtils.isEmpty(privilegeId)) {
				Privilege p = Utils.fetchPrivilege(privilegeId, userService);
				if (p == null) {
					throw new IllegalArgumentException();
				}
				type.setViewPrivilege(p);
			}
		}
		{
			String privilegeId = line.get(HEADER_EDIT_PRIV);
			if (!StringUtils.isEmpty(privilegeId)) {
				Privilege p = Utils.fetchPrivilege(privilegeId, userService);
				if (p == null) {
					throw new IllegalArgumentException();
				}
				type.setEditPrivilege(p);
			}
		}
		
		return type;
	}
}
