package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerLogFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

@OpenmrsProfile(modules = { "metadatasharing:*" })
public class MdsLoader extends BaseLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.MDS;
	}
	
	private final Log log = InitializerLogFactory.getLog(getClass());
	
	@Override
	public void load() {
		
		ConfigDirUtil dirUtil = getDirUtil();
		
		final PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		ImportConfig importConfig = new ImportConfig();
		importConfig.setPossibleMatch(ImportType.PREFER_THEIRS);
		importConfig.setExactMatch(ImportType.PREFER_THEIRS);
		importer.setImportConfig(importConfig);
		
		for (File file : dirUtil.getFiles("zip")) { // processing all the zip files inside the domain
			
			String fileName = dirUtil.getFileName(file.getPath());
			String checksum = dirUtil.getChecksumIfChanged(fileName);
			if (checksum.isEmpty()) {
				continue;
			}
			
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				importer.loadSerializedPackageStream(is);
				is.close();
				importer.importPackage();
				dirUtil.writeChecksum(fileName, checksum); // the updated config. file is marked as processed
				log.info("The following MDS package was succesfully imported: " + fileName);
			}
			catch (Exception e) {
				log.error("The MDS package could not be imported: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
