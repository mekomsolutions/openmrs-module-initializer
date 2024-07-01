package org.openmrs.module.initializer.api;

import java.util.UUID;

import org.junit.Test;
import org.openmrs.module.cohort.CohortType;
import org.openmrs.module.cohort.api.CohortTypeService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.cohort.ct.CohortTypeLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CohortTypeLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("cohort.cohortTypeService")
	private CohortTypeService cohortTypeService;
	
	@Autowired
	private CohortTypeLoader loader;
	
	@Test
	public void loader_shouldLoadCohortTypesAccordingToCsvFiles() {
		// setup
		{
			CohortType cohortType = new CohortType();
			cohortType.setUuid(UUID.randomUUID().toString());
			cohortType.setName("Old Personal");
			cohortType.setDescription("Personal list");
			cohortTypeService.saveCohortType(cohortType);
		}
		
		// replay
		loader.load();
		
		// verify
		{
			CohortType cohortType = cohortTypeService.getCohortTypeByName("System");
			
			assertThat(cohortType, notNullValue());
			assertThat(cohortType.getUuid(), equalTo("3ab0118c-ba0c-42df-ac96-c573c72eed5e"));
			assertThat(cohortType.getDescription(), equalTo("System lists"));
		}
		{
			CohortType cohortType = cohortTypeService.getCohortTypeByName("Personal");
			
			assertThat(cohortType, notNullValue());
			assertThat(cohortType.getUuid(), equalTo("9ebd4eb9-d9c6-4fd5-930a-30563fc5004c"));
			assertThat(cohortType.getDescription(), equalTo("Personal lists"));
		}
	}
	
}
