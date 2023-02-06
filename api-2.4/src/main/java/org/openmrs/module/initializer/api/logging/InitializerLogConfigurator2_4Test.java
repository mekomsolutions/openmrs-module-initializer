package org.openmrs.module.initializer.api.logging;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.openmrs.module.initializer.InitializerActivator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InitializerLogConfigurator2_4Test {
	
	@Test
	public void shouldSetupLoggerWithAppropriateNameAndLevel() throws ClassNotFoundException, InvocationTargetException,
	        InstantiationException, IllegalAccessException, NoSuchMethodException {
		// setup
		Level level = Level.WARN;
		InitializerLogConfigurator2_4 logConfigurator24 = new InitializerLogConfigurator2_4();
		
		// replay
		logConfigurator24.setupLogging(level, null);
		
		// verify
		assertEquals(org.apache.logging.log4j.Level.WARN,
		    LogManager.getLogger(InitializerActivator.class.getPackage().getName()).getLevel());
	}
	
}
