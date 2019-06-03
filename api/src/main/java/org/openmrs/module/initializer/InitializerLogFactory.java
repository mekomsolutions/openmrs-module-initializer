package org.openmrs.module.initializer;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openmrs.util.OpenmrsUtil;

/**
 * Custom logger that ensures that log messages are also routed through to the module's specific log
 * file. Use this log factory instead of the usual {@link LogFactory} when custom logging needs to
 * be enabled for a class.
 */
public class InitializerLogFactory {
	
	private static final String inizLogFilePath = Paths
	        .get(OpenmrsUtil.getApplicationDataDirectory(), InitializerConstants.MODULE_ARTIFACT_ID + ".log").toString();
	
	@SuppressWarnings("rawtypes")
	public static Log getLog(Class className) {
		
		Log log = LogFactory.getLog(className);
		
		final Logger logger = Logger.getLogger(className);
		try {
			logger.addAppender(
			    new FileAppender(new PatternLayout("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n"), inizLogFilePath, true));
			logger.setLevel((Level) Level.ALL);
		}
		catch (IOException e) {
			log.error("The custom logger could not be setup, defaulting on the usual logging mechanism.", e);
		}
		
		return log;
	}
	
}
