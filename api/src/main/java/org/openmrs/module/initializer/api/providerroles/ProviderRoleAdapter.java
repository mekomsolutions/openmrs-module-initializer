package org.openmrs.module.initializer.api.providerroles;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.User;

import java.util.Date;
import java.util.Set;

/**
 * This class provides an abstraction around a ProviderRole, whether that is the ProviderRole
 * defined in OpenMRS 2.8 without the providermanagement module running, the ProviderRole defined in
 * OpenMRS 2.8 that is extended by the providermanagement module, or the ProviderRole defined only
 * in the providermanagement module (providermanagement < 4.0.0 and core < 2.8.0)
 */
public class ProviderRoleAdapter extends BaseOpenmrsMetadata {
	
	private final OpenmrsMetadata providerRole;
	
	public ProviderRoleAdapter(OpenmrsMetadata providerRole) {
		this.providerRole = providerRole;
	}
	
	public OpenmrsMetadata getProviderRole() {
		return providerRole;
	}
	
	@Override
	public Integer getId() {
		return providerRole.getId();
	}
	
	@Override
	public void setId(Integer id) {
		providerRole.setId(id);
	}
	
	@Override
	public String getName() {
		return providerRole.getName();
	}
	
	@Override
	public void setName(String name) {
		providerRole.setName(name);
	}
	
	@Override
	public String getDescription() {
		return providerRole.getDescription();
	}
	
	@Override
	public void setDescription(String description) {
		providerRole.setDescription(description);
	}
	
	@Override
	public User getCreator() {
		return providerRole.getCreator();
	}
	
	@Override
	public void setCreator(User creator) {
		providerRole.setCreator(creator);
	}
	
	@Override
	public Date getDateCreated() {
		return providerRole.getDateCreated();
	}
	
	@Override
	public void setDateCreated(Date dateCreated) {
		providerRole.setDateCreated(dateCreated);
	}
	
	@Override
	public User getChangedBy() {
		return providerRole.getChangedBy();
	}
	
	@Override
	public void setChangedBy(User changedBy) {
		providerRole.setChangedBy(changedBy);
	}
	
	@Override
	public Date getDateChanged() {
		return providerRole.getDateChanged();
	}
	
	@Override
	public void setDateChanged(Date dateChanged) {
		providerRole.setDateChanged(dateChanged);
	}
	
	@Override
	public Boolean isRetired() {
		return providerRole.isRetired();
	}
	
	@Override
	public Boolean getRetired() {
		return providerRole.getRetired();
	}
	
	@Override
	public void setRetired(Boolean retired) {
		providerRole.setRetired(retired);
	}
	
	@Override
	public Date getDateRetired() {
		return providerRole.getDateRetired();
	}
	
	@Override
	public void setDateRetired(Date dateRetired) {
		providerRole.setDateRetired(dateRetired);
	}
	
	@Override
	public User getRetiredBy() {
		return providerRole.getRetiredBy();
	}
	
	@Override
	public void setRetiredBy(User retiredBy) {
		providerRole.setRetiredBy(retiredBy);
	}
	
	@Override
	public String getRetireReason() {
		return providerRole.getRetireReason();
	}
	
	@Override
	public void setRetireReason(String retireReason) {
		providerRole.setRetireReason(retireReason);
	}
	
	@Override
	public String getUuid() {
		return providerRole.getUuid();
	}
	
	@Override
	public void setUuid(String uuid) {
		providerRole.setUuid(uuid);
	}
	
	@Override
	public int hashCode() {
		return providerRole.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == providerRole.getClass() && providerRole.equals(obj);
	}
	
	@Override
	public String toString() {
		return providerRole.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Set<RelationshipType> getRelationshipTypes() {
		try {
			return (Set<RelationshipType>) PropertyUtils.getProperty(providerRole, "relationshipTypes");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setRelationshipTypes(Set<RelationshipType> relationshipTypes) {
		try {
			PropertyUtils.setProperty(providerRole, "relationshipTypes", relationshipTypes);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<OpenmrsObject> getSuperviseeProviderRoles() {
		try {
			return (Set<OpenmrsObject>) PropertyUtils.getProperty(providerRole, "superviseeProviderRoles");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setSuperviseeProviderRoles(Set<OpenmrsObject> superviseeProviderRoles) {
		try {
			PropertyUtils.setProperty(providerRole, "superviseeProviderRoles", superviseeProviderRoles);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<ProviderAttributeType> getProviderAttributeTypes() {
		try {
			return (Set<ProviderAttributeType>) PropertyUtils.getProperty(providerRole, "providerAttributeTypes");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setProviderAttributeTypes(Set<ProviderAttributeType> providerAttributeTypes) {
		try {
			PropertyUtils.setProperty(providerRole, "providerAttributeTypes", providerAttributeTypes);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
