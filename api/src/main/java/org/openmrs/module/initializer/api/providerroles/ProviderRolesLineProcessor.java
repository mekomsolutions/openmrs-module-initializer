package org.openmrs.module.initializer.api.providerroles;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

@OpenmrsProfile(modules = { "providermanagement:*" })
public class ProviderRolesLineProcessor extends BaseLineProcessor<ProviderRole> {
	
	public static final String HEADER_SUPERVISEE_ROLE_PREFIX = "Supervisee Provider Role";
	
	public static final String HEADER_RELATIONSHIP_TYPE_PREFIX = "Relationship Type";
	
	public static final String HEADER_PROVIDER_ATTRIBUTE_TYPE_PREFIX = "Provider Attribute Type";
	
	private ProviderManagementService providerManagementService;
	
	private ProviderService providerService;
	
	private PersonService personService;
	
	/**
	 * @param providerManagementService the ProviderManagementService
	 * @param providerService the ProviderService
	 * @param personService the PersonService
	 */
	@Autowired
	public ProviderRolesLineProcessor(ProviderManagementService providerManagementService, ProviderService providerService,
	    PersonService personService) {
		this.providerManagementService = providerManagementService;
		this.providerService = providerService;
		this.personService = personService;
	}
	
	@Override
	public ProviderRole fill(ProviderRole role, CsvLine line) throws IllegalArgumentException {
		role.setName(line.getName(true));
		role.setDescription(line.get(HEADER_DESC));
		role.setSuperviseeProviderRoles(new HashSet<>());
		role.setRelationshipTypes(new HashSet<>());
		role.setProviderAttributeTypes(new HashSet<>());
		
		for (String header : line.getHeaderLine()) {
			
			if (StringUtils.startsWithIgnoreCase(header, HEADER_SUPERVISEE_ROLE_PREFIX)) {
				String roleLookup = line.getString(header);
				if (StringUtils.isNotEmpty(roleLookup)) {
					ProviderRole superviseeRole = providerManagementService.getProviderRoleByUuid(roleLookup);
					if (superviseeRole == null) {
						throw new IllegalArgumentException("Unable to find supervisee provider role: " + roleLookup);
					}
					role.getSuperviseeProviderRoles().add(superviseeRole);
				}
			} else if (StringUtils.startsWithIgnoreCase(header, HEADER_RELATIONSHIP_TYPE_PREFIX)) {
				String typeLookup = line.getString(header);
				if (StringUtils.isNotEmpty(typeLookup)) {
					RelationshipType relationshipType = personService.getRelationshipTypeByUuid(typeLookup);
					if (relationshipType == null) {
						throw new IllegalArgumentException("Unable to find relationship type: " + typeLookup);
					}
					role.getRelationshipTypes().add(relationshipType);
				}
			} else if (StringUtils.startsWithIgnoreCase(header, HEADER_PROVIDER_ATTRIBUTE_TYPE_PREFIX)) {
				String typeLookup = line.getString(header);
				if (StringUtils.isNotEmpty(typeLookup)) {
					ProviderAttributeType attType = providerService.getProviderAttributeTypeByUuid(typeLookup);
					if (attType == null) {
						throw new IllegalArgumentException("Unable to find provider attribute type: " + typeLookup);
					}
					role.getProviderAttributeTypes().add(attType);
				}
			}
			
		}
		
		return role;
	}
}
