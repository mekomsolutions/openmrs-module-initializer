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
		InitializerLogConfigurator2_0 logConfigurator20 = new InitializerLogConfigurator2_0();
		Level level = Level.WARN;
		
		Filter levelRangeFilter = logConfigurator20.createLevelRangeFilter(level);
		
		Assert.assertEquals(level, ((LevelRangeFilter) levelRangeFilter).getLevelMax());
		
	}
	
}
