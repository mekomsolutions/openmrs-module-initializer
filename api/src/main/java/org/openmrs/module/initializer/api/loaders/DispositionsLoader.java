package org.openmrs.module.initializer.api.loaders;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@OpenmrsProfile(modules = { "emrapi:2.0.0-9.*" })
public class DispositionsLoader extends BaseFileLoader {
	
	@Autowired
	private DispositionService dispositionService;
	
	private boolean fileFound = false;
	
	@Override
	protected Domain getDomain() {
		return Domain.DISPOSITIONS;
	}
	
	@Override
	protected String getFileExtension() {
		return "json";
	}
	
	@Override
	public void load() {
		fileFound = false;
		super.load();
	}
	
	@Override
	protected void load(File file) throws Exception {
		if (fileFound) {
			throw new IllegalArgumentException(
			        "Multiple disposition files found in the disposition configuration directory.");
		}
		fileFound = true;
		dispositionService.setDispositionConfig("file:" + iniz.getBasePath().relativize(file.toPath()));
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		// skip checksums, this needs to be processed every time
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
}
