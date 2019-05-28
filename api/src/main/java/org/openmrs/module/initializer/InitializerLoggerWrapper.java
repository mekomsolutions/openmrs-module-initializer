package org.openmrs.module.initializer;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

public class InitializerLoggerWrapper implements Log {
	
	private Log log;
	
	private Logger logger;
	
	public InitializerLoggerWrapper(Log log, Logger logger) {
		this.log = log;
		this.logger = logger;
	}
	
	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}
	
	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}
	
	@Override
	public boolean isFatalEnabled() {
		return log.isFatalEnabled();
	}
	
	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}
	
	@Override
	public boolean isWarnEnabled() {
		return false;
	}
	
	@Override
	public void trace(Object o) {
		log.trace(o);
		logger.trace(o);
	}
	
	@Override
	public void trace(Object o, Throwable throwable) {
		log.trace(o, throwable);
		logger.trace(o, throwable);
	}
	
	@Override
	public void debug(Object o) {
		log.debug(o);
		logger.debug(o);
	}
	
	@Override
	public void debug(Object o, Throwable throwable) {
		log.debug(o, throwable);
		logger.debug(o, throwable);
	}
	
	@Override
	public void info(Object o) {
		log.info(o);
		logger.info(o);
	}
	
	@Override
	public void info(Object o, Throwable throwable) {
		log.info(o, throwable);
		logger.info(o, throwable);
	}
	
	@Override
	public void warn(Object o) {
		log.warn(o);
		logger.warn(o);
	}
	
	@Override
	public void warn(Object o, Throwable throwable) {
		log.warn(o, throwable);
		logger.warn(o, throwable);
	}
	
	@Override
	public void error(Object o) {
		log.error(o);
		logger.error(o);
	}
	
	@Override
	public void error(Object o, Throwable throwable) {
		log.error(o, throwable);
		logger.error(o, throwable);
	}
	
	@Override
	public void fatal(Object o) {
		log.fatal(o);
		logger.fatal(o);
	}
	
	@Override
	public void fatal(Object o, Throwable throwable) {
		log.fatal(o, throwable);
		logger.fatal(o, throwable);
	}
}
