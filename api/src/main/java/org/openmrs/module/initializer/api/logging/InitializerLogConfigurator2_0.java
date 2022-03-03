package org.openmrs.module.initializer.api.logging;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
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
		logger.addAppender(getFileAppender(level, logFilePath));
	}
	
	private Appender getFileAppender(Level level, Path logFilePath) {
		Appender defaultAppender = org.apache.log4j.Logger.getRootLogger().getAppender("DEBUGGING_FILE_APPENDER");
		Layout layout = defaultAppender == null ? new PatternLayout("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n")
		        : defaultAppender.getLayout();
		
		Appender appender = defaultAppender;
		try {
			appender = new FileAppender(layout, logFilePath.toString());
			appender.setName(logFilePath.getFileName().toString());
			
			// since the org.apache.log4j.varia package doesn't exist in the log4j2 bridge, we need to use reflection
			// so this class only has a runtime dependency on the LevelMatchFilter
			Filter levelMatchFilter = (Filter) Class.forName("org.apache.log4j.varia.LevelMatchFilter").getConstructor()
			        .newInstance();
			Method levelMatchSetter = levelMatchFilter.getClass().getMethod("setLevelToMatch", String.class);
			levelMatchSetter.invoke(levelMatchFilter, level.toString());
			
			appender.addFilter(levelMatchFilter);
		}
		catch (IOException | ClassCastException | ClassNotFoundException | InvocationTargetException | IllegalAccessException
		        | NoSuchMethodException | InstantiationException e) {
			log.error("The custom log file appender could not be setup for {}.", MODULE_NAME, e);
		}
		
		return appender;
	}
}
