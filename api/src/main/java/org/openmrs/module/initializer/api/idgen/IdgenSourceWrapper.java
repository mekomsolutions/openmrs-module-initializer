package org.openmrs.module.initializer.api.idgen;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.idgen.IdentifierSource;

/**
 * This wrapper class is necessary because {@link IdentifierSource} does not extend
 * {@linkBaseOpenmrsObject}.
 */
public class IdgenSourceWrapper extends BaseOpenmrsObject {
	
	private static final long serialVersionUID = 1L;
	
	protected IdentifierSource source;
	
	public IdgenSourceWrapper(IdentifierSource source) {
		super();
		this.source = source;
	}
	
	public IdentifierSourceType getType() {
		return BaseIdentifierSourceLineProcessor.getIdentifierSourceType(source);
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
}
