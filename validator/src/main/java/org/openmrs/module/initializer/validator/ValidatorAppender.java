package org.openmrs.module.initializer.validator;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class ValidatorAppender extends AppenderSkeleton {
	
	@Override
	public void close() {
	}
	
	@Override
	public boolean requiresLayout() {
		return false;
	}
	
	@Override
	protected void append(LoggingEvent event) {
		if (Level.ERROR.equals(event.getLevel())) {
			Validator.errors.add(event);
		}
	}
	
}
