package org.openmrs.module.initializer;

import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class InitializerLog implements Log {
	
	private Logger logger;
	
	public InitializerLog(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
	
	@Override
	public boolean isErrorEnabled() {
		return logger.isEnabledFor(Level.ERROR);
	}
	
	@Override
	public boolean isFatalEnabled() {
		return logger.isEnabledFor(Level.FATAL);
	}
	
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
	
	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}
	
	@Override
	public boolean isWarnEnabled() {
		return logger.isEnabledFor(Level.WARN);
	}
	
	@Override
	public void trace(Object o) {
		logger.trace(o);
	}
	
	@Override
	public void trace(Object o, Throwable throwable) {
		logger.trace(o, throwable);
	}
	
	@Override
	public void debug(Object o) {
		logger.debug(o);
	}
	
	@Override
	public void debug(Object o, Throwable throwable) {
		logger.debug(o, throwable);
	}
	
	@Override
	public void info(Object o) {
		logger.info(o);
	}
	
	@Override
	public void info(Object o, Throwable throwable) {
		logger.info(o, throwable);
	}
	
	@Override
	public void warn(Object o) {
		logger.warn(o);
	}
	
	@Override
	public void warn(Object o, Throwable throwable) {
		logger.warn(o, throwable);
	}
	
	@Override
	public void error(Object o) {
		logger.error(o);
	}
	
	@Override
	public void error(Object o, Throwable throwable) {
		logger.error(o, throwable);
	}
	
	@Override
	public void fatal(Object o) {
		logger.fatal(o);
	}
	
	@Override
	public void fatal(Object o, Throwable throwable) {
		logger.fatal(o, throwable);
	}
}
