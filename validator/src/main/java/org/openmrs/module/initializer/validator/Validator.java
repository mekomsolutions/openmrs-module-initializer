package org.openmrs.module.initializer.validator;

import static org.apache.commons.lang.StringUtils.endsWith;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.replace;
import static org.apache.commons.lang.StringUtils.startsWith;
import static org.openmrs.module.initializer.InitializerConstants.ARG_DOMAINS;
import static org.openmrs.module.initializer.InitializerConstants.ARG_EXCLUDE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.runner.JUnitCore;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.utils.Utils;

public class Validator {
	
	public static CommandLine cmdLine;
	
	public static final String ARG_CONFIG_DIR = "config-dir";
	
	public static final String ARG_CIEL_PATH = "ciel-path";
	
	public static final String ARG_HELP = "help";
	
	public static final String ARG_CHECKSUMS = "checksums";
	
	public static final String ARG_VERBOSE = "verbose";
	
	public static Set<LoggingEvent> errors = new HashSet<>();
	
	/**
	 * Properly escapes the single quotes of a piece of SQL for MySQL. Strings ending with a ";" are
	 * assumed to be the last piece of a SQL instruction.
	 * 
	 * @param sqlPiece A whole SQL instruction or a piece of SQL instruction
	 * @return The piece of SQL with single quotes properly escaped
	 * @see https://stackoverflow.com/a/64878054/321797
	 */
	public static String escapeSingleQuotes(String sqlPiece) {
		boolean needsReappending = false;
		if (endsWith(sqlPiece, ";")) {
			needsReappending = true;
			sqlPiece = removeEnd(sqlPiece, ";");
		}
		return replace(sqlPiece, "\\'", "''") + (needsReappending ? ";" : "");
	}
	
	/**
	 * Turns an original CIEL SQL dump into a HSQLDB-friendly one.
	 */
	public static File trimCielSqlFile(File originalCielSqlFile) throws IOException {
		File trimmedCielSqlFile = File.createTempFile("iniz-validator-trimmed-ciel", ".sql");
		trimmedCielSqlFile.deleteOnExit();
		
		final BufferedWriter writer = new BufferedWriter(new FileWriter(trimmedCielSqlFile));
		for (String line : Files.readAllLines(Paths.get(originalCielSqlFile.getAbsolutePath()), Charsets.UTF_8)) {
			if (!startsWith(line, "--") && !startsWith(line, "/*!40") && !isEmpty(line)) {
				writer.write(escapeSingleQuotes(line + "\n"));
			}
		}
		writer.close();
		
		return trimmedCielSqlFile;
	}
	
	public static File getJarFile() throws URISyntaxException {
		CodeSource codeSource = Validator.class.getProtectionDomain().getCodeSource();
		return new File(codeSource.getLocation().toURI().getPath());
	}
	
	public static String getJarDirPath() throws URISyntaxException {
		File jarFile = getJarFile();
		String jarDir = jarFile.getParentFile().getPath();
		return jarDir;
	}
	
	public static Options getCLIOptions() {
		final StringBuilder sb = new StringBuilder();
		Stream.of(Domain.values()).forEach(d -> {
			sb.append(d.getName() + ",");
		});
		final String domainsCsv = removeEnd(sb.toString(), ",");
		
		Options options = new Options();
		options.addOption(Option.builder("h").longOpt(ARG_HELP).desc("Prints help.").build());
		options.addOption(Option.builder("c").hasArg().longOpt(ARG_CONFIG_DIR).argName("DIR")
		        .desc("The path to the OpenMRS config directory.").build());
		options.addOption(Option.builder("l").longOpt(ARG_CIEL_PATH).hasArg().argName("FILE")
		        .desc("The path to the CIEL .sql file.").build());
		options.addOption(Option.builder("d").longOpt(ARG_DOMAINS).hasArg().argName("CSV")
		        .desc("A CSV string of selected domains to selectively include, eg.: metadatasharing,concepts,roles ;"
		                + "\nor to selectively exclude, eg.: '!metadatasharing,concepts,roles' ;"
		                + "\nomit the argument altogether to process all domains." + "\nAvailable domains: " + domainsCsv)
		        .build());
		
		Stream.of(Domain.values()).forEach(d -> {
			options.addOption(Option.builder().longOpt(ARG_EXCLUDE + "." + d.getName()).hasArg().argName("CSV")
			        .desc("A CSV string of wildcard file exclusion patterns to apply to the '" + d.getName() + "' domain."
			                + "\nEg.: '*foo*.bar,*f00?.baz'")
			        .build());
		});
		options.addOption(Option.builder("s").longOpt("checksums")
		        .desc("Enables writing the checksum files in a checksum folder besides the configuration folder.").build());
		options.addOption(Option.builder("D").longOpt(ARG_VERBOSE).desc("Enables verbose logging.").build());
		return options;
	}
	
	public static void main(String[] args) throws URISyntaxException, ParseException {
		// setting up logging
		{
			org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("org.openmrs.module.initializer");
			logger.addAppender(Utils.getFileAppender(Paths.get(getJarDirPath(), "initializer.log")));
			logger.addAppender(new ValidatorAppender());
			logger.setLevel(org.apache.log4j.Level.WARN);
			
			org.apache.log4j.Logger.getLogger("org.openmrs").setLevel(org.apache.log4j.Level.INFO);
			org.apache.log4j.Logger.getLogger("org.openmrs.api").setLevel(org.apache.log4j.Level.INFO);
		}
		
		// processing args
		{
			Options options = getCLIOptions();
			cmdLine = new DefaultParser().parse(options, args);
			
			if (ArrayUtils.isEmpty(cmdLine.getOptions()) || cmdLine.hasOption(ARG_HELP)) {
				HelpFormatter f = new HelpFormatter();
				f.setWidth(f.getWidth() * 2);
				f.printHelp(getJarFile().getName(), options);
				return;
			}
			
			if (cmdLine.hasOption(ARG_VERBOSE)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.DEBUG);
			}
		}
		
		// Testing the config
		JUnitCore.main(ConfigurationTester.class.getCanonicalName());
		
	}
}
