package org.openmrs.module.initializer.api.pat;

import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PersonAttributeTypeLineProcessor extends BaseLineProcessor<PersonAttributeType> {
	
	protected static class Helper {
		
		public static final int FOREIGNKEY_UNFOUND = 0;
		
		public int getForeignKey(String formatClass, String uuid) {
			int foreignKey = FOREIGNKEY_UNFOUND;
			if (formatClass.equals("org.openmrs.Concept")) {
				foreignKey = getForeignKey(Context.getConceptService(), uuid);
			}
			// TODO implement fetching the foreign keys IDs for further format classes
			return foreignKey;
		}
		
		public static int getForeignKey(ConceptService cs, String uuid) {
			Concept concept = cs.getConceptByUuid(uuid);
			if (concept != null) {
				return concept.getId();
			} else {
				return FOREIGNKEY_UNFOUND;
			}
		}
		
		public Privilege getPrivilege(String privilege) throws IllegalArgumentException {
			try {
				return Context.getUserService().getPrivilege(privilege);
			}
			catch (APIException e) {
				throw new IllegalArgumentException("'" + privilege + "' is not a valid privilege.", e);
			}
		}
	}
	
	protected static String HEADER_FORMAT = "format";
	
	protected static String HEADER_FOREIGN_UUID = "foreign uuid";
	
	protected static String HEADER_SEARCHABLE = "searchable";
	
	protected static String HEADER_EDITPRIVILEGE = "edit privilege";
	
	protected Helper helper;
	
	private PersonService personService;
	
	@Autowired
	public PersonAttributeTypeLineProcessor(@Qualifier("personService") PersonService personService) {
		this.personService = personService;
		this.helper = new Helper();
	}
	
	public void setHelper(Helper helper) {
		this.helper = helper;
	}
	
	@Override
	protected PersonAttributeType bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		PersonAttributeType pat = personService.getPersonAttributeTypeByUuid(uuid);
		
		if (pat == null) {
			pat = new PersonAttributeType();
			if (!StringUtils.isEmpty(uuid)) {
				pat.setUuid(uuid);
			}
		}
		
		pat.setRetired(getVoidOrRetire(line.asLine()));
		
		return pat;
	}
	
	@Override
	protected PersonAttributeType fill(PersonAttributeType pat, CsvLine line) throws IllegalArgumentException {
		
		pat.setName(line.get(HEADER_NAME));
		pat.setDescription(line.get(HEADER_DESC));
		pat.setSearchable(BooleanUtils.toBoolean(line.get(HEADER_SEARCHABLE)));
		
		String formatClass = line.get(HEADER_FORMAT);
		if (!StringUtils.isEmpty(formatClass)) {
			try {
				Class.forName(formatClass);
			}
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("'" + formatClass + "' does not represent a valid Java or OpenMRS class.",
				        e);
			}
			pat.setFormat(formatClass);
		}
		
		String foreignUuid = line.get(HEADER_FOREIGN_UUID);
		if (!StringUtils.isEmpty(foreignUuid)) {
			foreignUuid = UUID.fromString(foreignUuid).toString();
			pat.setForeignKey(helper.getForeignKey(formatClass, foreignUuid));
		}
		
		String editPrivilege = line.get(HEADER_EDITPRIVILEGE);
		if (!StringUtils.isEmpty(editPrivilege)) {
			pat.setEditPrivilege(helper.getPrivilege(editPrivilege));
		}
		
		return pat;
	}
}
