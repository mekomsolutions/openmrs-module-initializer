package org.openmrs.module.initializer.api.loaders;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.openconceptlab.Import;
import org.openmrs.module.openconceptlab.ImportService;
import org.openmrs.module.openconceptlab.importer.Importer;

import java.io.File;
import java.util.concurrent.TimeUnit;
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
		
		// This is just a sanity check, this is never expected to be true
		while (lastImport == importService.getLastImport()) {
			log.warn("Waiting for OCL to start import");
			TimeUnit.SECONDS.sleep(1);
		}
		
		Import oclImport = importService.getLastImport();
		
		// This is just a sanity check, this is never expected to be true
		while (!oclImport.isStopped()) {
			log.debug("OCL import: " + importer.getBytesProcessed() + " / " + importer.getTotalBytesToProcess());
			TimeUnit.SECONDS.sleep(1);
			oclImport = importService.getLastImport();
		}
		
		// If the import stopped with errors, then throw an exception
		if (StringUtils.isNotBlank(oclImport.getErrorMessage())) {
			throw new IllegalStateException(oclImport.getErrorMessage());
		}
		
		log.debug("OCL import completed successfully: " + oclImport.getLocalDateStopped());
	}
	
}
