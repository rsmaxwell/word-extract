package com.rsmaxwell.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

	private static CommandLine getCommandLine(String[] args) throws ParseException {

		// @formatter:off
		Option version = Option.builder("v")
				               .longOpt("version")
				               .argName("version")
				               .desc("show program version")
				               .build();
		
		Option help = Option.builder("h")
				            .longOpt("help")
				            .argName("help")
				            .desc("show program help")
				            .build();
		
		Option inputFile = Option.builder("i")
				            .longOpt("inputFile")
				            .argName("inputFile")
				            .hasArg()
				            .desc("set the input word file (*.docx)")
				            .build();
		
		Option outputFile = Option.builder("o")
                            .longOpt("outputDir")
                            .argName("outputDir")
                            .hasArg()
                            .desc("set the output dir")
                            .build();
		
		Option workingDir = Option.builder("w")
	                        .longOpt("workingDir")
	                        .argName("workingDir")
	                        .hasArg()
	                        .desc("set the working directory")
	                        .build();
		// @formatter:on

		Options options = new Options();
		options.addOption(version);
		options.addOption(help);
		options.addOption(inputFile);
		options.addOption(workingDir);
		options.addOption(outputFile);

		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("version")) {
			System.out.println("version:   " + Version.version());
			System.out.println("buildID:   " + Version.buildID());
			System.out.println("buildDate: " + Version.buildDate());
			System.out.println("gitCommit: " + Version.gitCommit());
			System.out.println("gitBranch: " + Version.gitBranch());
			System.out.println("gitURL:    " + Version.gitURL());

		} else if (line.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("extractor <OPTION> ", options);
		}

		return line;
	}

	private static void clearWorkingDirectory(String relativeWorkingDirName) throws IOException {
		File relativeWorkingDir = new File(relativeWorkingDirName);
		String workingDirName = relativeWorkingDir.getCanonicalPath();
		Path workingDir = Paths.get(workingDirName);

		if (Files.exists(workingDir)) {
			Files.walk(workingDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}

		if (Files.exists(workingDir)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		Files.createDirectory(workingDir);
	}

	public static void main(String[] args) throws Exception {

		CommandLine line = getCommandLine(args);

		if (!line.hasOption('i')) {
			System.out.println("Missing required option -i | --inputFile");
			return;
		}

		String wordFilename = line.getOptionValue("i");
		String wordBaseFilename = getBaseName(wordFilename);

		String outputDirName = line.getOptionValue("o", "./output");
		File dependanciesDir = new File(outputDirName, "dependancies");
		dependanciesDir.mkdirs();
		File fragmentDir = new File(outputDirName, "fragments");
		fragmentDir.mkdirs();

		String workingDirName = line.getOptionValue("w", "./working");
		clearWorkingDirectory(workingDirName);

		Extractor extractor = Extractor.INSTANCE;
		extractor.order = wordBaseFilename;
		extractor.reference = wordBaseFilename;

		extractor.unzip(wordFilename, workingDirName);
		extractor.toJson(wordFilename, workingDirName, dependanciesDir, fragmentDir);
	}

	private static String getBaseName(String filename) {
		File file = new File(filename);
		String fileName = file.getName();
		if (fileName.indexOf(".") > 0) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		return fileName;
	}
}
