package org.openmrs.module.initializer;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;

import java.io.File;

public abstract class DomainBaseModuleContextSensitive_2_7_Test extends DomainBaseModuleContextSensitiveTest {
	
	@Override
	public void initModules() {
		// Only the optional modules that have a Platform 3.0 release are started, see Platform3ModuleContextLoader
		{
			Module mod = new Module("", "fhir2", "", "", "", "1.6.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "idgen", "", "", "", "4.6.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "metadatamapping", "", "", "", "1.3.4", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "cohort", "", "", "", "3.5.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
	}
}
