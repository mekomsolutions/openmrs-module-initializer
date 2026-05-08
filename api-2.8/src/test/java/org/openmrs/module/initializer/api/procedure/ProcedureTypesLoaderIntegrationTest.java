package org.openmrs.module.initializer.api.procedure;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.emrapi.procedure.ProcedureService;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_8_Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

// emrapi 3.3+ ships org.openmrs.module.emrapi.account.StringToProviderRoleConverter as a plain
// @Component, which collides on the default bean name 'stringToProviderRoleConverter' with
// providermanagement's converter — both end up in the merged test Spring context and Spring
// throws ConflictingBeanDefinitionException. Re-enable this test once emrapi qualifies its
// converter's bean name (or we exclude providermanagement from this module's test classpath).
@Ignore("Blocked by upstream emrapi/providermanagement bean-name conflict — see class header")
public class ProcedureTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_8_Test {
	
	@Autowired
	@Qualifier("procedureService")
	private ProcedureService procedureService;
	
	@Autowired
	private ProcedureTypesLoader loader;
	
	@Test
	public void load_shouldLoadProcedureTypesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verify create-by-uuid
		{
			ProcedureType type = procedureService.getProcedureTypeByUuid("9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b1234");
			Assert.assertNotNull(type);
			Assert.assertEquals("Appendectomy", type.getName());
			Assert.assertEquals("Surgical removal of the appendix", type.getDescription());
			Assert.assertFalse(type.getRetired());
		}
		
		// Verify create-without-uuid (lookup by name on subsequent runs)
		{
			List<ProcedureType> matches = procedureService.getProcedureTypesByName("Cholecystectomy");
			Assert.assertNotNull(matches);
			Assert.assertEquals(1, matches.size());
			Assert.assertEquals("Surgical removal of the gallbladder", matches.get(0).getDescription());
		}
		
		// Verify retire
		{
			ProcedureType type = procedureService.getProcedureTypeByUuid("9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b5678");
			Assert.assertNotNull(type);
			Assert.assertTrue(type.getRetired());
		}
	}
}
