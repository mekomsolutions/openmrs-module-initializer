package org.openmrs.module.initializer.api.logging;

import java.io.Serializable;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.InitializerActivator;
import org.springframework.stereotype.Component;

@Component
@OpenmrsProfile(openmrsPlatformVersion = "2.1.5 - 2.1.*, 2.4.* - 2.*")
public class InitializerLoggingConfigurator2_4 extends InitializerLogConfigurator {
	
	/**
	 * Returns a ready-to-use appender to log to a custom file.
	 * 
	 * @param logFilePath The path to the log file.
	 * @return The appender to be added to any logger.
	 */
	private static Appender getFileAppender(Path logFilePath) {
		Logger rootLogger = (Logger) LogManager.getRootLogger();
		Appender defaultAppender = rootLogger.getAppenders().values().iterator().next();
		Layout<? extends Serializable> layout = defaultAppender == null
		        ? PatternLayout.newBuilder().withPattern("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n").build()
		        : defaultAppender.getLayout();
		
		Appender appender = FileAppender.newBuilder().setName(logFilePath.getFileName().toString())
		        .withFileName(logFilePath.toString()).setLayout(layout).build();
		
		appender.start();
		
		return appender;
	}
	
	@Override
	public void setupLogging(org.apache.log4j.Level level, Path logFilePath) {
		setupLogging(Level.toLevel(level.toString()), logFilePath);
	}
	
	public void setupLogging(Level level, Path logFilePath) {
		if (logFilePath == null) {
			logFilePath = getDefaultLogFile();
		}
		
		Logger logger = (Logger) LogManager.getLogger(InitializerActivator.class.getPackage().getName());
		logger.addAppender(getFileAppender(logFilePath));
		logger.setLevel(level);
	}
}
