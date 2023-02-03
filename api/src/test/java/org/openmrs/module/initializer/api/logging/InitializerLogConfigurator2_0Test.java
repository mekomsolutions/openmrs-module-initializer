package org.openmrs.module.initializer.api.logging;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class InitializerLogConfigurator2_0Test {
	
	@Test
	public void createLevelRangeFilter_shouldCreateLevelWithMaxValue() throws ClassNotFoundException, InvocationTargetException,
	        InstantiationException, IllegalAccessException, NoSuchMethodException {
		// setup
		Level level = Level.WARN;
		InitializerLogConfigurator2_0 logConfigurator20 = new InitializerLogConfigurator2_0();

		// replay
		Filter levelRangeFilter = logConfigurator20.createLevelRangeFilter(level);

		// verif
		Assert.assertEquals(level, ((LevelRangeFilter) levelRangeFilter).getLevelMax());
		
	}
	
}
