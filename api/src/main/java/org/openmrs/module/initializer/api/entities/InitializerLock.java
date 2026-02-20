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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "initializer_lock")
public class InitializerLock {
	
	@Id
	@Column(name = "lock_name")
	private String lockName;
	
	@Column(name = "lock_until", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lockUntil;
	
	@Column(name = "locked_by", length = 255)
	private String lockedBy;
	
	public String getLockName() {
		return lockName;
	}
	
	public void setLockName(String lockName) {
		this.lockName = lockName;
	}
	
	public Date getLockUntil() {
		return lockUntil;
	}
	
	public void setLockUntil(Date lockUntil) {
		this.lockUntil = lockUntil;
	}
	
	public String getLockedBy() {
		return lockedBy;
	}
	
	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}
}
