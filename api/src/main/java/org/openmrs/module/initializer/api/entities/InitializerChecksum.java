package org.openmrs.module.initializer.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "initializer_checksums")
public class InitializerChecksum {
	
	@Id
	@Column(name = "file_path", length = 512)
	private String filePath;
	
	@Column(name = "checksum", nullable = false, length = 64)
	private String checksum;
	
	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	public InitializerChecksum() {
	}
	
	public InitializerChecksum(String filePath, String checksum) {
		this.filePath = filePath;
		this.checksum = checksum;
		this.updatedAt = new Date();
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
