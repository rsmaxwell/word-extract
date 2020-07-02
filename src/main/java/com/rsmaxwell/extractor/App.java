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
		
		Option rootDir = Option.builder("r")
				            .longOpt("root")
				            .argName("root directory")
				            .hasArg()
				            .desc("root directory")
				            .build();

		Option pathDir = Option.builder("r")
				            .longOpt("path")
				            .argName("path directory")
				            .hasArg()
				            .desc("path directory")
				            .build();

		Option wordFile = Option.builder("w")
	                        .longOpt("word")
	                        .argName("word file")
	                        .hasArg()
	                        .desc("word file")
	                        .build();
		
		Option diary = Option.builder("d")
				            .longOpt("diary")
				            .argName("diary name")
				            .hasArg()
				            .desc("set the diary name")
				            .build();
		
		Option imageFile = Option.builder("i")
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
		options.addOption(rootDir);
		options.addOption(pathDir);
		options.addOption(wordFile);
		options.addOption(diary);
		options.addOption(imageFile);
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

		if (!line.hasOption('r')) {
			System.out.println("Missing required option -r | --root");
			return;
		}

		if (!line.hasOption('p')) {
			System.out.println("Missing required option -p | --path");
			return;
		}
		String rootDirName = line.getOptionValue("r");
		String pathDirName = line.getOptionValue("r");
		File rootPathDir = new File(rootDirName, pathDirName);
		if (!rootPathDir.exists()) {
			throw new Exception("file not found: " + rootPathDir);
		}

		if (!line.hasOption('w')) {
			System.out.println("Missing required option -w | --wordFile");
			return;
		}
		String wordFilename = line.getOptionValue("w");

		String outputDirName = line.getOptionValue("o", "output");

		if (!line.hasOption('d')) {
			System.out.println("Missing required option -d | --diary");
			return;
		}
		String diary = line.getOptionValue("d", "");

		if (!line.hasOption('i')) {
			System.out.println("Missing required option -i | --imageFilename");
			return;
		}
		String imageFilename = line.getOptionValue("i", "");

		String wordFileName = rootDirName + "/" + pathDirName + "/" + diary + "/metadata/word/" + wordFilename;
		File wordFile = new File(wordFileName);
		if (!wordFile.exists()) {
			throw new Exception("file not found: " + wordFileName);
		}

		Extractor extractor = new Extractor(wordFileName, outputDirName);
		Extractor.instance = extractor;

		extractor.summary();
		extractor.unzip();
		extractor.toJson(wordFilename, diary, imageFilename);
	}
}
