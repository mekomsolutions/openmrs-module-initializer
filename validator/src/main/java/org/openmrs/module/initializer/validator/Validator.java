package org.openmrs.module.initializer.validator;

import static org.apache.commons.lang.StringUtils.endsWith;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.replace;
import static org.apache.commons.lang.StringUtils.startsWith;
import static org.apache.log4j.Level.INFO;
import static org.apache.log4j.Level.WARN;
import static org.openmrs.module.initializer.InitializerConstants.ARG_DOMAINS;
import static org.openmrs.module.initializer.InitializerConstants.ARG_EXCLUDE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {
	
	protected static final Logger log = LoggerFactory.getLogger(Validator.class);
	
	public static CommandLine cmdLine;
	
	private static Path logFilePath = null;
	
	public static final String ARG_CONFIG_DIR = "config-dir";
	
	public static final String ARG_CIEL_FILE = "ciel-file";
	
	public static final String ARG_LOG_DIR = "log-dir";
	
	public static final String ARG_HELP = "help";
	
	public static final String ARG_CHECKSUMS = "checksums";
	
	public static final String ARG_VERBOSE = "verbose";
	
	public static final String ARG_LOGGING_LEVEL = "logging-level";
	
	public static final String ARG_UNSAFE = "unsafe";
	
	/*
	 * Keeps an internal count of the logged errors
	 */
	static Set<LoggingEvent> errors = new HashSet<>();
	
	static boolean unsafe = false;
	
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
	
	/**
	 * Convenience method giving the path of the Validator JAR file.
	 * 
	 * @return The path of the Validator JAR file.
	 */
	public static Path getJarDirPath() throws Exception {
		return Paths.get(getJarFile().getParentFile().getPath());
	}
	
	private static Options getCLIOptions() {
		final StringBuilder sb = new StringBuilder();
		Stream.of(Domain.values()).forEach(d -> {
			sb.append(d.getName() + ",");
		});
		final String domainsCsv = removeEnd(sb.toString(), ",");
		
		Options options = new Options();
		options.addOption(Option.builder("h").longOpt(ARG_HELP).desc("Prints help.").build());
		options.addOption(Option.builder("u").longOpt(ARG_UNSAFE)
		        .desc("Toggles the unsafe mode that fails early as soon as a domain loading error occurs.").build());
		options.addOption(Option.builder("c").hasArg().longOpt(ARG_CONFIG_DIR).argName("DIR")
		        .desc("The path to the OpenMRS config directory.").build());
		options.addOption(Option.builder("l").longOpt(ARG_CIEL_FILE).hasArg().argName("FILE")
		        .desc("The path to the CIEL .sql file.").build());
		options.addOption(Option.builder("f").longOpt(ARG_LOG_DIR).hasArg().argName("DIR")
		        .desc("The path to the log file directory.").build());
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
		options.addOption(Option.builder("V").longOpt(ARG_VERBOSE).desc("Enables verbose logging.").build());
		options.addOption(Option.builder("L").hasArg().longOpt(ARG_LOGGING_LEVEL).argName("ARG")
		        .desc("The verbose mode logging level: " + Level.TRACE + ", " + Level.DEBUG + " or " + Level.INFO + ".")
		        .build());
		return options;
	}
	
	/**
	 * @return A valid Path object to the log file, or null if no log file could not be written to.
	 */
	public static Path getLogFilePath() {
		return logFilePath;
	}
	
	public static void setupLog4j(Level level, Path logFilePath) {
		org.apache.log4j.Logger.getRootLogger().setLevel(level);
		
		org.apache.log4j.Logger.getLogger("org.openmrs").setLevel(level);
		org.apache.log4j.Logger.getLogger("org.openmrs.api").setLevel(level);
		
		org.apache.log4j.Logger inizLogger = org.apache.log4j.Logger.getLogger("org.openmrs.module.initializer");
		inizLogger.setLevel(WARN); // this is to focus the Inititalizer log file to the most relevant messages
		if (logFilePath != null) {
			inizLogger.addAppender(Utils.getFileAppender(logFilePath));
		}
		inizLogger.addAppender(new ValidatorAppender());
	}
	
	/**
	 * Sets the log file path {@link Path} instance based on a suggested log file directory path. If the
	 * suggested log file dir cannot be used, this will fallback in order to 1) the JAR file folder or
	 * eventually 2) to a temp folder.
	 * 
	 * @param logFileDir The suggested log file directory path.
	 */
	protected static void setLogFilePath(String logFileDir) {
		
		logFilePath = null;
		
		// log dir part
		Path path = null;
		try {
			if (isEmpty(logFileDir)) {
				throw new IllegalArgumentException(); //this is just to get in the catch
			}
			path = Paths.get(logFileDir);
			if (!Files.exists(path)) {
				path = Files.createDirectory(path);
			}
		}
		catch (Exception e0) {
			if (!isEmpty(logFileDir)) {
				log.error("There was an error accessing the specified log file folder " + path
				        + ".\nFalling back on using the JAR file folder...");
			}
			try {
				path = getJarDirPath();
			}
			catch (Exception e1) {
				log.warn(
				    "There was an error accessing the JAR file folder for the log file.\nFalling back on using a temp directory...");
				try {
					path = Files.createTempDirectory("");
				}
				catch (Exception e2) {
					log.warn(
					    "There was an error creating a temp directory for the log file. No log file will be written, please refer to the console output log instead.");
					return;
				}
			}
		}
		// log file part
		path = path.resolve("initializer.log");
		try {
			Files.deleteIfExists(path);
			path = Files.createFile(path);
		}
		catch (Exception e) {
			log.error("There was an error creating the log file at " + path
			        + "\nNo log file will be written, please refer to the console.");
			return;
		}
		
		log.info("The log file will be written at " + path);
		
		logFilePath = path;
	}
	
	/**
	 * Main API method to execute a dry run of a configuration and collect the JUnit {@link Result}.
	 * 
	 * @param args The Validator CLI args.
	 * @return The JUnit Result object.
	 */
	public static Result getJUnitResult(String[] args) throws IllegalArgumentException, URISyntaxException, ParseException {
		// default logging
		setupLog4j(INFO, null);
		
		Options options = getCLIOptions();
		cmdLine = new DefaultParser().parse(options, args);
		
		Level level = INFO;
		if (cmdLine.hasOption(ARG_VERBOSE)) {
			level = Level.toLevel(cmdLine.getOptionValue(ARG_LOGGING_LEVEL));
			level = level.isGreaterOrEqual(INFO) ? INFO : level; // verbose means at least INFO level
		}
		
		// setting up logging
		setLogFilePath(cmdLine.getOptionValue(ARG_LOG_DIR));
		setupLog4j(level, getLogFilePath());
		
		if (ArrayUtils.isEmpty(cmdLine.getOptions()) || cmdLine.hasOption(ARG_HELP)) {
			HelpFormatter f = new HelpFormatter();
			f.setWidth(f.getWidth() * 2);
			f.printHelp(getJarFile().getName(), options);
			return new Result();
		}
		
		if (!cmdLine.hasOption(ARG_CONFIG_DIR)) {
			throw new IllegalArgumentException("The path to the configuration directory was not provided.");
		}
		
		unsafe = cmdLine.hasOption(ARG_UNSAFE) ? true : false;
		
		// Testing the config
		return JUnitCore.runClasses(ConfigurationTester.class);
	}
	
	public static void main(String[] args) {
		try {
			Result result = getJUnitResult(args);
			System.exit(result.wasSuccessful() ? 0 : 1);
		}
		catch (Throwable t) {
			log.error(t.getMessage(), t);
			System.exit(1);
		}
	}
}
