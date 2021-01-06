package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenmrsProfile(modules = { "metadatasharing:*" })
public class MdsLoader extends BaseLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected Domain getDomain() {
		return Domain.MDS;
	}
	
	@Override
	public void load(List<String> wildcardExclusions) {
		
		ConfigDirUtil dirUtil = getDirUtil();
		
		final PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		ImportConfig importConfig = new ImportConfig();
		importConfig.setPossibleMatch(ImportType.PREFER_THEIRS);
		importConfig.setExactMatch(ImportType.PREFER_THEIRS);
		importer.setImportConfig(importConfig);
		
		for (File file : dirUtil.getFiles("zip", wildcardExclusions)) { // processing all the zip files inside the domain
			
			String checksum = dirUtil.getChecksumIfChanged(file);
			if (checksum.isEmpty()) {
				continue;
			}
			
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				importer.loadSerializedPackageStream(is);
				is.close();
				importer.importPackage();
				dirUtil.writeChecksum(file, checksum); // the updated config. file is marked as processed
				log.info("The following MDS package was succesfully imported: " + file.getPath());
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
