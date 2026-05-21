package org.openmrs.module.initializer;

import java.io.File;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;

public abstract class DomainBaseModuleContextSensitive_2_8_Test extends DomainBaseModuleContextSensitiveTest {
	
	@Override
	public void initModules() {
		Module mod = new Module("", "emrapi", "", "", "", "3.4.0", "");
		mod.setFile(new File(""));
		ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
	}
}
