package com.rsmaxwell.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsmaxwell.diaryjson.Fragment;
import com.rsmaxwell.diaryjson.OutputDocument;
import com.rsmaxwell.extractor.parser.MyDocument;

public class Extractor {

	public int year;
	public int month;
	public int day;
	public String order;
	public String source;

	private String inputDirName;
	private File inputDir;

	private String outputDirName;
	private File outputDir;

	private String workingDirName;
	private File workingDir;

	private String dependanciesDirName;
	private File dependanciesDir;

	private String fragmentBaseName;
	private File fragmentBase;

	private String templateDirName;
	private File templateDir;

	public static Extractor instance;

	private static Extractor getInstance() {
		return instance;
	}

	public Extractor(String inputDirName, String outputDirName) {

		this.inputDirName = inputDirName;
		inputDir = new File(inputDirName);

		this.workingDirName = outputDirName + "/working";
		workingDir = new File(workingDirName);

		this.dependanciesDirName = outputDirName + "/dependancies";
		dependanciesDir = new File(dependanciesDirName);
		dependanciesDir.mkdirs();

		this.fragmentBaseName = outputDirName + "/fragments";
		fragmentBase = new File(fragmentBaseName);
		fragmentBase.mkdirs();

		templateDirName = inputDir + "/templates";
		templateDir = new File(templateDirName);
	}

	public void unzip(String archive) throws IOException {

		clearWorkingDirectory(workingDir);

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(archive));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {

			if ("word/document.xml".contentEquals(zipEntry.getName())) {

				String filename = workingDirName + "/" + zipEntry.getName();
				File file = new File(filename);
				File parentFolder = new File(file.getParent());
				parentFolder.mkdirs();

				File newFile = newFile(workingDir, zipEntry);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}

			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	private static void clearWorkingDirectory(File dir) throws IOException {

		Path path = dir.toPath();

		if (Files.exists(path)) {
			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}

		if (Files.exists(path)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		Files.createDirectory(path);
	}

	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	public void toJson(String wordPathname) throws Exception {

		// ---------------------------------------------------------------------
		// Find the year which this word file refers to
		// ---------------------------------------------------------------------
		this.year = FindYear.get(wordPathname);
		this.order = getBasename(wordPathname);
		this.source = wordPathname;

		// ---------------------------------------------------------------------
		// Parse the MS Word file into an output document
		// ---------------------------------------------------------------------
		String inputFilename = workingDirName + "/word/document.xml";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(inputFilename);

		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();

		MyDocument document = MyDocument.create(root, 0);
		OutputDocument outputDocument = document.toOutput();

		// ---------------------------------------------------------------------
		// Create a buffer to collect the dependencies
		// ---------------------------------------------------------------------
		Set<String> deps = new HashSet<String>();

		// ---------------------------------------------------------------------
		// Write out the fragments to disk
		// ---------------------------------------------------------------------
		ObjectMapper objectMapper = new ObjectMapper();
		for (Fragment fragment : outputDocument.fragments) {

			if (fragment.html == null) {
				throw new Exception("null html found in fragment: " + fragment);
			}

			String dirName = fragment.getDirectoryName();

			// ---------------------------------------------------------------------
			// Write out the fragment as a json info file and a separate text file
			// with the html content
			// ---------------------------------------------------------------------
			String fragmentDirName = fragmentBaseName + "/" + dirName;
			File fragmentDir = new File(fragmentDirName);
			fragmentDir.mkdirs();

			File jsonFile = new File(fragmentDir, "fragment.json");
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, fragment);

			Path htmlPath = new File(fragmentDir, "fragment.html").toPath();
			try (BufferedWriter writer = Files.newBufferedWriter(htmlPath)) {
				writer.write(fragment.html);
			}

			// ---------------------------------------------------------------------
			// This fragment depends on the word file, so ad it as a dependancy
			// ---------------------------------------------------------------------
			deps.add(fragmentDirName + "/fragment.json");
		}

		// ---------------------------------------------------------------------
		// Write out the makefile
		// ---------------------------------------------------------------------
		String separator = "";
		StringBuilder sb = new StringBuilder();
		for (String dep : deps) {
			sb.append(separator);
			sb.append(dep);
			separator = " ";
		}

		sb.append(" &: ");
		sb.append(wordPathname);
		sb.append("\n");
		sb.append("\t./extract $^");
		sb.append("\n");

		String basename = getBasename(wordPathname);

		File dependancyFile = new File(dependanciesDir, basename + ".mk");
		try (FileWriter dependancyWriter = new FileWriter(dependancyFile, false);) {
			PrintWriter dependancyPrintWriter = new PrintWriter(dependancyWriter);
			dependancyPrintWriter.println(sb.toString());
		}
	}

	public static void touch(File file) throws IOException {
		long timestamp = System.currentTimeMillis();
		touch(file, timestamp);
	}

	public static void touch(File file, long timestamp) throws IOException {
		if (!file.exists()) {
			new FileOutputStream(file).close();
		}

		file.setLastModified(timestamp);
	}

	private static String getBasename(String pathname) {
		File file = new File(pathname);
		String filename = file.getName();
		return removeExtension(filename);
	}

	public static String getExtension(String filename) {
		String extension = "";
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			extension = filename.substring(i + 1);
		}
		return extension;
	}

	public static String removeExtension(String filename) {
		String result = filename;
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			result = filename.substring(0, i);
		}
		return result;
	}
}
