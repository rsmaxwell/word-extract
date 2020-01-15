package com.rsmaxwell.extract;

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

	public static void main(String[] args) throws ParseException, IOException {

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
		
		Option file = Option.builder("f")
				            .longOpt("file")
				            .argName("file")
				            .hasArg()
				            .desc("set the input word file")
				            .build();
		// @formatter:on

		Options options = new Options();
		options.addOption(version);
		options.addOption(help);
		options.addOption(file);

		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("version")) {
			System.out.println("version: 0.0.1");
		} else if (line.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("extractor <OPTION> ", options);
		} else {
			File relative = new File("./unzipped");
			String destDirName = relative.getCanonicalPath();
			Path destDir = Paths.get(destDirName);

			System.err.println("destDirName = " + destDirName);

			if (Files.exists(destDir)) {
				Files.walk(destDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}

			if (Files.exists(destDir)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			Files.createDirectory(destDir);

			String filename = line.getOptionValue("f");
			Extractor extractor = new Extractor();
			extractor.unzip(filename, destDirName);
			extractor.parse(destDirName, "word");
		}
	}
}
