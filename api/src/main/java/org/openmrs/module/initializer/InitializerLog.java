package org.openmrs.module.initializer;

import org.apache.commons.logging.Log;
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
	public boolean isFatalEnabled() {
		return isErrorEnabled();
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
	public boolean isErrorEnabled() {
		return true;
	}
	
	@Override
	public boolean isWarnEnabled() {
		return true;
	}
	
	@Override
	public void trace(Object message) {
		logger.trace((String) message);
	}
	
	@Override
	public void trace(Object message, Throwable t) {
		logger.trace((String) message, t);
	}
	
	@Override
	public void debug(Object message) {
		logger.debug((String) message);
	}
	
	@Override
	public void debug(Object message, Throwable t) {
		logger.debug((String) message, t);
	}
	
	@Override
	public void info(Object message) {
		logger.info((String) message);
	}
	
	@Override
	public void info(Object message, Throwable t) {
		logger.info((String) message, t);
	}
	
	@Override
	public void warn(Object message) {
		logger.warn((String) message);
	}
	
	@Override
	public void warn(Object message, Throwable t) {
		logger.warn((String) message, t);
	}
	
	@Override
	public void error(Object message) {
		logger.error((String) message);
	}
	
	@Override
	public void error(Object message, Throwable t) {
		logger.error((String) message, t);
	}
	
	@Override
	public void fatal(Object message) {
		logger.error((String) message);
	}
	
	@Override
	public void fatal(Object message, Throwable t) {
		logger.error((String) message, t);
	}
	
}
