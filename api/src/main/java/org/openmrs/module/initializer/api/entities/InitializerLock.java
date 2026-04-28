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
