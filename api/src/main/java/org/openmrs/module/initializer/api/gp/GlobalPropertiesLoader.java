package org.openmrs.module.initializer.api.gp;

import java.io.InputStream;

import org.openmrs.api.AdministrationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.module.initializer.api.loaders.BaseInputStreamLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalPropertiesLoader extends BaseInputStreamLoader {
	
	@Autowired
	private AdministrationService adminService;
	
	@Override
	protected Domain getDomain() {
		return Domain.GLOBAL_PROPERTIES;
	}
	
	@Override
	protected String getFileExtension() {
		return "xml";
	}
	
	@Override
	protected void load(InputStream is) throws Exception {
		GlobalPropertiesConfig config = InitializerSerializer.getGlobalPropertiesConfig(is);
		adminService.saveGlobalProperties(config.getGlobalProperties());
	}
}
