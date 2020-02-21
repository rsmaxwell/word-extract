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
		
		Option wordFilename = Option.builder("w")
				            .longOpt("word")
				            .argName("word document")
				            .hasArg()
				            .desc("set the word file (*.docx)")
				            .build();
		
		Option imageFilename = Option.builder("i")
				            .longOpt("image")
				            .argName("image filename")
				            .hasArg()
				            .desc("set the image file (*.jpg)")
				            .build();
		
		Option outputFile = Option.builder("o")
                            .longOpt("outputDir")
                            .argName("outputDir")
                            .hasArg()
                            .desc("set the output dir")
                            .build();
		// @formatter:on

		Options options = new Options();
		options.addOption(version);
		options.addOption(help);
		options.addOption(wordFilename);
		options.addOption(imageFilename);
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

		String outputDirName = line.getOptionValue("o", "output");

		if (!line.hasOption('i')) {
			System.out.println("Missing required option -i | --imageFilename");
			return;
		}
		String imageFilename = line.getOptionValue("i", "");

		Extractor extractor = new Extractor(outputDirName);
		Extractor.instance = extractor;

		extractor.unzip(wordFileName);
		extractor.toJson(wordFileName, imageFilename);
	}
}
