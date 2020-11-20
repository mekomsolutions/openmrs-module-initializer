package org.openmrs.module.initializer.validator;

import static org.mockito.Mockito.when;
import static org.openmrs.module.initializer.Domain.CONCEPTS;
import static org.openmrs.module.initializer.Domain.ENCOUNTER_TYPES;

import java.util.Arrays;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.module.initializer.api.InitializerService;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationTestTest {
	
	@Mock
	private InitializerService iniz;
	
	private ConfigurationTest test;
	
	@Before
	public void before() throws ParseException {
		
		Options options = Validator.getCLIOptions();
		String[] args = { "--config-dir=/tmp/configuration" };
		Validator.cmdLine = new DefaultParser().parse(options, args);
		
		test = new ConfigurationTest();
		test.setService(iniz);
		
		when(iniz.getLoaders()).thenReturn(Arrays.asList(new TestLoader(CONCEPTS), new TestLoader(ENCOUNTER_TYPES)));
		
	}
	
	@Test
	public void should() {
		
		test.loadConfiguration();
		
	}
	
}
