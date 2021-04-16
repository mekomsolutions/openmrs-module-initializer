package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.zip.ZipFile;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.openconceptlab.importer.Importer;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "ocl:*" })
public class OpenConceptLabLoader extends BaseFileLoader {
	
	@Autowired
	private Importer importer;
	
	@Override
	protected Domain getDomain() {
		return Domain.OCL;
	}
	
	@Override
	protected String getFileExtension() {
		return "zip";
	}
	
	@Override
	protected void load(File file) throws Exception {
		ZipFile zip = new ZipFile(file);
		importer.run(zip);
	}
	
}
