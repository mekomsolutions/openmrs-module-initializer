package org.openmrs.module.initializer.api.logging;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.openmrs.util.OpenmrsUtil;

import static org.openmrs.module.initializer.InitializerConstants.MODULE_ARTIFACT_ID;

public interface InitializerLogConfigurator {
	
	/**
	 * Method called to setup Initializer logging. Should provide a Level, which is the level of
	 * filtering applied to the appender, and a path where the log file will be written.
	 *
	 * @param level Allow log message of this level or higher to be written to the appender
	 * @param logFilePath The path the log file should be created at
	 */
	void setupLogging(Level level, Path logFilePath);
	
	default Path getDefaultLogFile() {
		return Paths.get(OpenmrsUtil.getApplicationDataDirectory(), MODULE_ARTIFACT_ID + ".log");
	}
}
