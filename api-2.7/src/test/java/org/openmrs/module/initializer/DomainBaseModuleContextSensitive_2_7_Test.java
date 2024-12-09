package org.openmrs.module.initializer;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;

import java.io.File;

public abstract class DomainBaseModuleContextSensitive_2_7_Test extends DomainBaseModuleContextSensitiveTest {
	
	@Override
	public void initModules() {
		{
			Module mod = new Module("", "addresshierarchy", "", "", "", "2.17.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "exti18n", "", "", "", "1.0.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "fhir2", "", "", "", "1.6.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "openconceptlab", "", "", "", "1.2.9", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "htmlformentry", "", "", "", "4.0.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "idgen", "", "", "", "4.6.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "metadatasharing", "", "", "", "1.2.2", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "metadatamapping", "", "", "", "1.3.4", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "appointments", "", "", "", "1.2", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "datafilter", "", "", "", "1.0.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "bahmni.ie.apps", "", "", "", "1.0.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "providermanagement", "", "", "", "1.0.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "cohort", "", "", "", "3.5.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "emrapi", "", "", "", "2.0.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
	}
}
