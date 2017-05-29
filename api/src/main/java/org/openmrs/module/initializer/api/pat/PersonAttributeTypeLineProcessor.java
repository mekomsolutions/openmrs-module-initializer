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

public class PersonAttributeTypeLineProcessor extends BaseLineProcessor<PersonAttributeType, PersonService> {
	
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
	
	public PersonAttributeTypeLineProcessor(String[] headerLine, PersonService ps) {
		this(headerLine, ps, new Helper());
	}
	
	public PersonAttributeTypeLineProcessor(String[] headerLine, PersonService ps, Helper mapper) {
		super(headerLine, ps);
		this.helper = mapper;
	}
	
	@Override
	protected PersonAttributeType fill(PersonAttributeType pat, String[] line) throws IllegalArgumentException {
		
		pat.setName(line[getColumn(HEADER_NAME)]);
		
		try {
			pat.setDescription(line[getColumn(HEADER_DESC)]);
		}
		catch (IllegalArgumentException e) {}
		
		try {
			pat.setSearchable(BooleanUtils.toBoolean(line[getColumn(HEADER_SEARCHABLE)]));
		}
		catch (IllegalArgumentException e) {}
		
		String formatClass = line[getColumn(HEADER_FORMAT)];
		if (!StringUtils.isEmpty(formatClass)) {
			try {
				Class.forName(formatClass);
			}
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(
				        "'" + formatClass + "' does not represent a valid Java or OpenMRS class.", e);
			}
			pat.setFormat(formatClass);
		}
		
		String foreignUuid = "";
		try {
			foreignUuid = line[getColumn(HEADER_FOREIGN_UUID)];
		}
		catch (IllegalArgumentException e) {}
		if (!StringUtils.isEmpty(foreignUuid)) {
			foreignUuid = UUID.fromString(foreignUuid).toString();
			pat.setForeignKey(helper.getForeignKey(formatClass, foreignUuid));
		}
		
		String editPrivilege = "";
		try {
			editPrivilege = line[getColumn(HEADER_EDITPRIVILEGE)];
		}
		catch (IllegalArgumentException e) {}
		if (!StringUtils.isEmpty(editPrivilege)) {
			pat.setEditPrivilege(helper.getPrivilege(editPrivilege));
		}
		
		return pat;
	}
}
