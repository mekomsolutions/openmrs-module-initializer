package org.openmrs.module.initializer.api.datafilter.mappings;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.OpenmrsObject;

public class DataFilterMapping extends BaseOpenmrsMetadata {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id = 0;
	
	private OpenmrsObject entity;
	
	private OpenmrsObject basis;
	
	public DataFilterMapping(OpenmrsObject entity, OpenmrsObject basis) {
		super();
		this.entity = entity;
		this.basis = basis;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public OpenmrsObject getEntity() {
		return entity;
	}
	
	public void setEntity(OpenmrsObject entity) {
		this.entity = entity;
	}
	
	public OpenmrsObject getBasis() {
		return basis;
	}
	
	public void setBasis(OpenmrsObject basis) {
		this.basis = basis;
	}
}
