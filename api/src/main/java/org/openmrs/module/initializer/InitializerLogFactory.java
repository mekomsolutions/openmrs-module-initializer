package org.openmrs.module.initializer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openmrs.util.OpenmrsUtil;

public class InitializerLogFactory {
	
	private static final String inizLogFilePath = Paths
	        .get(OpenmrsUtil.getApplicationDataDirectory(), InitializerConstants.MODULE_ARTIFACT_ID + ".log").toString();
	
	private static Set<Class> classesWithLogger = new HashSet<Class>();
	
	@SuppressWarnings("rawtypes")
	public static Log getLog(Class className) {
		
		Log log = LogFactory.getLog(className);
		
		final Logger logger = Logger.getLogger(className);
		try {
			if (!classesWithLogger.contains(className)) {
				logger.addAppender(
				    new FileAppender(new PatternLayout("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n"), inizLogFilePath, true));
				logger.setLevel((Level) Level.ALL);
			}
		}
		catch (IOException e) {
			log.error("The custom logger could not be setup, defaulting on the usual logging mechanism.", e);
		}
		
		return log;
	}
	
}
