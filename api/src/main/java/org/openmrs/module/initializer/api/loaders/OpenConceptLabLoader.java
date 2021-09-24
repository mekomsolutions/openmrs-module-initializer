package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.zip.ZipFile;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.openconceptlab.importer.Importer;

@OpenmrsProfile(modules = { "openconceptlab:1.2.9" })
public class OpenConceptLabLoader extends BaseFileLoader {
	
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
		Importer importer = Context.getRegisteredComponent("openconceptlab.importer", Importer.class);
		importer.run(zip);
	}
	
}
