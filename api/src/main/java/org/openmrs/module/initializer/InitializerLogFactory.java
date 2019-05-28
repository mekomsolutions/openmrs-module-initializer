package org.openmrs.module.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openmrs.util.OpenmrsUtil;

import java.io.IOException;

public class InitializerLogFactory {
	
	static String targetLog = OpenmrsUtil.getApplicationDataDirectory() + "initializer.log";
	
	static FileAppender inizAppender = null;
	
	static Log log = null;
	
	/**
	 * SetUp new InitializerLoggerWrapper instance.
	 * 
	 * @param className class which called InitializerLogFactory
	 * @return loggerWrapper instance.
	 * @throws IOException
	 */
	private static Log setUpLog(Class className) throws IOException {
		Log log = LogFactory.getLog(className);
		Logger logger = Logger.getLogger(className);
		inizAppender = new FileAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n"), targetLog, true);
		logger.addAppender(inizAppender);
		logger.setLevel((Level) Level.ALL);
		return new InitializerLoggerWrapper(log, logger);
	}
	
	/**
	 * @param className class which called InitializerLogFactory
	 * @return Custom Log instance.
	 */
	public static Log getLog(Class className) {
		if (log == null) {
			try {
				log = setUpLog(className);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return log;
	}
	
}
