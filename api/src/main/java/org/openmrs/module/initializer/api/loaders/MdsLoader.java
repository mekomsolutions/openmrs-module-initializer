package org.openmrs.module.initializer.api.loaders;

import java.io.InputStream;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

@OpenmrsProfile(modules = { "metadatasharing:*" })
public class MdsLoader extends BaseInputStreamLoader {
	
	private PackageImporter importer;
	
	private PackageImporter getImporter() {
		if (importer == null) {
			importer = MetadataSharing.getInstance().newPackageImporter();
			ImportConfig importConfig = new ImportConfig();
			importConfig.setPossibleMatch(ImportType.PREFER_THEIRS);
			importConfig.setExactMatch(ImportType.PREFER_THEIRS);
			importer.setImportConfig(importConfig);
		}
		return importer;
	}
	
	@Override
	protected Domain getDomain() {
		return Domain.METADATASHARING;
	}
	
	@Override
	protected String getFileExtension() {
		return "zip";
	}
	
	@Override
	protected void load(InputStream is) throws Exception {
		getImporter().loadSerializedPackageStream(is);
		getImporter().importPackage();
	}
	
}
