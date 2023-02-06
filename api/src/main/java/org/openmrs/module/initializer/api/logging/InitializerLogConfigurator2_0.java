package org.openmrs.module.initializer.api.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.Filter;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.InitializerActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.InitializerConstants.MODULE_NAME;

@Component
@OpenmrsProfile(openmrsPlatformVersion = "1.* - 2.1.4, 2.2.* - 2.3.*")
public class InitializerLogConfigurator2_0 implements InitializerLogConfigurator {
	
	private static final Logger log = LoggerFactory.getLogger(InitializerLogConfigurator2_0.class);
	
	public void setupLogging(Level level, Path logFilePath) {
		if (logFilePath == null) {
			logFilePath = getDefaultLogFile();
		}
		
		org.apache.log4j.Logger logger = org.apache.log4j.Logger
		        .getLogger(InitializerActivator.class.getPackage().getName());
		logger.addAppender(getFileAppender(logFilePath));
		logger.setLevel(level);
	}
	
	private Appender getFileAppender(Path logFilePath) {
		Appender defaultAppender = org.apache.log4j.Logger.getRootLogger().getAppender("DEBUGGING_FILE_APPENDER");
		Layout layout = defaultAppender == null ? new PatternLayout("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n")
		        : defaultAppender.getLayout();
		
		Appender appender = defaultAppender;
		try {
			appender = (Appender) Class.forName("org.apache.log4j.FileAppender").getConstructor(Layout.class, String.class)
			        .newInstance(layout, logFilePath.toString());
			appender.setName(logFilePath.getFileName().toString());
		}
		catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException
		        | InstantiationException | RuntimeException e) {
			log.error("The custom log file appender could not be setup for {}.", MODULE_NAME, e);
		}
		
		return appender;
	}
}
