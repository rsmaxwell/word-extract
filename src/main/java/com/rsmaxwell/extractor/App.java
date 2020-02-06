package com.rsmaxwell.extractor;

import java.io.File;

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
		
		Option wordFile = Option.builder("w")
				            .longOpt("wordFile")
				            .argName("wordFile")
				            .hasArg()
				            .desc("set the word file (*.docx)")
				            .build();
		
		Option outputFile = Option.builder("o")
                            .longOpt("outputDir")
                            .argName("outputDir")
                            .hasArg()
                            .desc("set the output dir")
                            .build();
		
		Option inputDir = Option.builder("i")
                            .longOpt("inputDir")
                            .argName("inputDir")
                            .hasArg()
                            .desc("set the input dir")
                            .build();
		// @formatter:on

		Options options = new Options();
		options.addOption(version);
		options.addOption(help);
		options.addOption(wordFile);
		options.addOption(inputDir);
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

	public static void main(String[] args) throws Exception {

		CommandLine line = getCommandLine(args);

		if (!line.hasOption('w')) {
			System.out.println("Missing required option -w | --wordFile");
			return;
		}

		String wordFileName = line.getOptionValue("w");
		File wordFile = new File(wordFileName);
		if (!wordFile.exists()) {
			throw new Exception("file not found: " + wordFileName);
		}

		String inputDirName = line.getOptionValue("i", "input");
		File inputDir = new File(inputDirName);
		if (!inputDir.exists()) {
			throw new Exception("dir not found: " + inputDirName);
		}

		String outputDirName = line.getOptionValue("o", "output");

		Extractor extractor = new Extractor(inputDirName, outputDirName);
		Extractor.instance = extractor;

		extractor.unzip(wordFileName);
		extractor.toJson(wordFileName);
	}
}
