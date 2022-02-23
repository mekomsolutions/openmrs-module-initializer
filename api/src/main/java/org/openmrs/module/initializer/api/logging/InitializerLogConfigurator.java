package org.openmrs.module.initializer.api.logging;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.InitializerActivator;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.InitializerConstants.MODULE_ARTIFACT_ID;
import static org.openmrs.module.initializer.InitializerConstants.MODULE_NAME;

@Component
@OpenmrsProfile(openmrsPlatformVersion = "1.* - 2.3.*")
public class InitializerLogConfigurator {
	
	private static final Logger log = LoggerFactory.getLogger(InitializerLogConfigurator.class);
	
	/**
	 * Returns a ready-to-use appender to log to a custom file.
	 * 
	 * @param logFilePath The path to the log file.
	 * @return The appender to be added to any logger.
	 */
	private static Appender getFileAppender(Path logFilePath) {
		Appender defaultAppender = org.apache.log4j.Logger.getRootLogger().getAppender("DEBUGGING_FILE_APPENDER");
		Layout layout = defaultAppender == null ? new PatternLayout("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n")
		        : defaultAppender.getLayout();
		
		Appender appender = defaultAppender;
		try {
			appender = new FileAppender(layout, logFilePath.toString());
			appender.setName(logFilePath.getFileName().toString());
		}
		catch (IOException e) {
			log.error("The custom log file appender could not be setup for {}.", MODULE_NAME, e);
		}
		
		return appender;
	}
	
	public void setupLogging(Level level, Path logFilePath) {
		if (logFilePath == null) {
			logFilePath = getDefaultLogFile();
		}
		
		org.apache.log4j.Logger logger = org.apache.log4j.Logger
		        .getLogger(InitializerActivator.class.getPackage().getName());
		logger.addAppender(getFileAppender(logFilePath));
		logger.setLevel(level);
	}
	
	protected Path getDefaultLogFile() {
		return Paths.get(OpenmrsUtil.getApplicationDataDirectory(), MODULE_ARTIFACT_ID + ".log");
	}
	
}
