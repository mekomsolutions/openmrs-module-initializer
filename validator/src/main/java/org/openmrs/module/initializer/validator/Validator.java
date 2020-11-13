package org.openmrs.module.initializer.validator;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.startsWith;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
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

import groovy.json.internal.Charsets;

public class Validator {
	
	public static List<String> arguments;
	
	public static Set<LoggingEvent> errors = new HashSet<>();
	
	/**
	 * Turns an original CIEL SQL dump into a one line, comment-less set of SQL instructions.
	 */
	public static File trimCielSqlFile(File originalCielSqlFile) throws IOException {
		File trimmedCielSqlFile = File.createTempFile("iniz-validator-trimmed-ciel", ".sql");
		trimmedCielSqlFile.deleteOnExit();
		
		final BufferedWriter writer = new BufferedWriter(new FileWriter(trimmedCielSqlFile));
		for (String line : Files.readAllLines(Paths.get(originalCielSqlFile.getAbsolutePath()), Charsets.UTF_8)) {
			if (!startsWith(line, "--") && !startsWith(line, "/*!40") && !isEmpty(line)) {
				writer.write(line);
			}
		}
		writer.close();
		
		return trimmedCielSqlFile;
	}
	
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
