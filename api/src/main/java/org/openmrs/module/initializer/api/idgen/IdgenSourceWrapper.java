package org.openmrs.module.initializer.api.idgen;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.idgen.IdentifierSource;

/**
 * This wrapper class is necessary because {@link IdentifierSource} does not extend
 * {@link BaseOpenmrsMetadata}.
 */
public class IdgenSourceWrapper extends BaseOpenmrsMetadata {
	
	private static final long serialVersionUID = 1L;
	
	protected IdentifierSource source;
	
	public IdgenSourceWrapper(IdentifierSource source) {
		super();
		this.source = source;
	}
	
	public IdentifierSourceType getType() {
		return IdentifierSourceLineProcessor.getIdentifierSourceType(source);
	}
	
	public IdentifierSource getIdentifierSource() {
		return source;
	}
	
	public void setIdentifierSource(IdentifierSource identifierSource) {
		this.source = identifierSource;
	}
	
	@Override
	public Integer getId() {
		return source.getId();
	}
	
	@Override
	public void setId(Integer id) {
		source.setId(id);
	}
	
	@Override
	public void setRetired(Boolean retired) {
		source.setRetired(retired);
	}
	
	@Override
	public Boolean isRetired() {
		return source.isRetired();
	}
	
}
