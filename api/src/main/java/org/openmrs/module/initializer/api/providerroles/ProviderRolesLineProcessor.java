package org.openmrs.module.initializer.api.providerroles;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ProviderRolesLineProcessor extends BaseLineProcessor<OpenmrsMetadata> {
	
	public static final String HEADER_SUPERVISEE_ROLE_PREFIX = "Supervisee Provider Role";
	
	public static final String HEADER_RELATIONSHIP_TYPE_PREFIX = "Relationship Type";
	
	public static final String HEADER_PROVIDER_ATTRIBUTE_TYPE_PREFIX = "Provider Attribute Type";
	
	private final ProviderService providerService;
	
	private final PersonService personService;
	
	private final ProviderRoleServiceAdapter service = new ProviderRoleServiceAdapter();
	
	/**
	 * @param providerService the ProviderService
	 * @param personService the PersonService
	 */
	@Autowired
	public ProviderRolesLineProcessor(ProviderService providerService, PersonService personService) {
		this.providerService = providerService;
		this.personService = personService;
	}
	
	@Override
	public OpenmrsMetadata fill(OpenmrsMetadata role, CsvLine line) throws IllegalArgumentException {
		ProviderRoleAdapter roleAdapter = new ProviderRoleAdapter(role);
		roleAdapter.setName(line.getName(true));
		roleAdapter.setDescription(line.get(HEADER_DESC));
		roleAdapter.setSuperviseeProviderRoles(new HashSet<>());
		roleAdapter.setRelationshipTypes(new HashSet<>());
		roleAdapter.setProviderAttributeTypes(new HashSet<>());
		
		for (String header : line.getHeaderLine()) {
			
			if (StringUtils.startsWithIgnoreCase(header, HEADER_SUPERVISEE_ROLE_PREFIX)) {
				String roleLookup = line.getString(header);
				if (StringUtils.isNotEmpty(roleLookup)) {
					OpenmrsObject superviseeRole = service.getProviderRoleByUuid(roleLookup);
					if (superviseeRole == null) {
						throw new IllegalArgumentException("Unable to find supervisee provider role: " + roleLookup);
					}
					roleAdapter.getSuperviseeProviderRoles().add(superviseeRole);
				}
			} else if (StringUtils.startsWithIgnoreCase(header, HEADER_RELATIONSHIP_TYPE_PREFIX)) {
				String typeLookup = line.getString(header);
				if (StringUtils.isNotEmpty(typeLookup)) {
					RelationshipType relationshipType = personService.getRelationshipTypeByUuid(typeLookup);
					if (relationshipType == null) {
						throw new IllegalArgumentException("Unable to find relationship type: " + typeLookup);
					}
					roleAdapter.getRelationshipTypes().add(relationshipType);
				}
			} else if (StringUtils.startsWithIgnoreCase(header, HEADER_PROVIDER_ATTRIBUTE_TYPE_PREFIX)) {
				String typeLookup = line.getString(header);
				if (StringUtils.isNotEmpty(typeLookup)) {
					ProviderAttributeType attType = providerService.getProviderAttributeTypeByUuid(typeLookup);
					if (attType == null) {
						throw new IllegalArgumentException("Unable to find provider attribute type: " + typeLookup);
					}
					roleAdapter.getProviderAttributeTypes().add(attType);
				}
			}
			
		}
		
		return roleAdapter.getProviderRole();
	}
}
