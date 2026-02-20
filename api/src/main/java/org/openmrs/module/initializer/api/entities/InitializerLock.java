/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "initializer_lock")
public class InitializerLock {
	
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "locked", nullable = false)
	private Boolean locked;
	
	@Column(name = "locked_at")
	private Date lockedAt;
	
	@Column(name = "locked_by")
	private String lockedBy;
	
	public Boolean getLocked() {
		return locked;
	}
	
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	public Date getLockedAt() {
		return lockedAt;
	}
	
	public void setLockedAt(Date lockedAt) {
		this.lockedAt = lockedAt;
	}
	
	public String getLockedBy() {
		return lockedBy;
	}
	
	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
}
