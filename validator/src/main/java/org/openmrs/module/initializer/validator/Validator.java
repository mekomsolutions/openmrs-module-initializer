package org.openmrs.module.initializer.validator;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.runner.JUnitCore;
import org.openmrs.module.initializer.api.utils.Utils;

public class Validator {
	
	public static List<String> arguments;
	
	public static Set<LoggingEvent> errors = new HashSet<>();
	
	public static void main(String[] args) throws URISyntaxException {
		
		CodeSource codeSource = Validator.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		String jarDir = jarFile.getParentFile().getPath();
		
		Logger logger = Logger.getLogger("org.openmrs.module.initializer");
		logger.addAppender(Utils.getFileAppender(Paths.get(jarDir, "initializer.log")));
		logger.addAppender(new ValidatorAppender());
		logger.setLevel(Level.WARN);
		
		arguments = Collections.unmodifiableList(Arrays.asList(args));
		
		//		JUnitCore junit = new JUnitCore();
		//		Result result = junit.run(ConfigValidationTest.class);
		//		System.out.println("Success: " + result.wasSuccessful());
		
		JUnitCore.main(ConfigurationTest.class.getCanonicalName());
		
	}
}
