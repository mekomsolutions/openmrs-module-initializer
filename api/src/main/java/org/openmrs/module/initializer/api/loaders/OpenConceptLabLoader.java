package org.openmrs.module.initializer.api.loaders;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.openconceptlab.Import;
import org.openmrs.module.openconceptlab.ImportService;
import org.openmrs.module.openconceptlab.importer.Importer;

import java.io.File;
import java.util.zip.ZipFile;

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
		ImportService importService = Context.getService(ImportService.class);
		
		Import lastImport = importService.getLastImport();
		log.debug("Starting OCL importer");
		importer.run(zip);
		Import oclImport = importService.getLastImport();
		
		// Import failed to start.  This can happen another import is already currently running
		if (oclImport == null || oclImport.equals(lastImport)) {
			throw new IllegalStateException("OCL import did not start successfully");
		}

		// Import detected errors
		if (StringUtils.isNotBlank(oclImport.getErrorMessage())) {
			throw new IllegalStateException(oclImport.getErrorMessage());
		}
		
		// Import never stopped
		if (!oclImport.isStopped()) {
			throw new IllegalStateException("OCL import did not complete successfully");
		}

		log.debug("OCL import completed successfully: " + oclImport.getLocalDateStopped());
	}
	
}
