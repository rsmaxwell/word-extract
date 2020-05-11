package com.rsmaxwell.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsmaxwell.diaryjson.OutputDocument;
import com.rsmaxwell.diaryjson.fragment.Fragment;
import com.rsmaxwell.extractor.parser.MyDocument;

public class Extractor {

	public int year;
	public int month;
	public int day;
	public String order;
	public String wordFilename;
	public String imageFilename;
	public String diary;

	private String wordFileName;
	private File wordFile;

	private String workingDirName;
	private File workingDir;

	public String baseDirName;
	private File fragmentBase;

	public static Extractor instance;

	public Extractor(String wordFileName, String outputDirName) {

		this.wordFileName = wordFileName;
		wordFile = new File(wordFileName);

		this.workingDirName = outputDirName + "/working";
		workingDir = new File(workingDirName);

		this.baseDirName = outputDirName + "/fragments";
		fragmentBase = new File(baseDirName);
		fragmentBase.mkdirs();
	}

	public void summary() throws IOException {
		System.out.println("Extractor: " + Version.version());
		System.out.println("Reading: " + wordFile.getCanonicalPath());
		System.out.println("Writing: " + fragmentBase.getCanonicalPath());
	}

	public void unzip() throws IOException {

		clearWorkingDirectory(workingDir);

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(wordFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {

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

	public void toJson(String wordFilename, String diary, String imageFilename) throws Exception {

		// ---------------------------------------------------------------------
		// Find the year which this word file refers to
		// ---------------------------------------------------------------------
		this.year = FindYear.get(wordFilename);
		this.order = getBasename(wordFilename);
		this.wordFilename = wordFilename;
		this.diary = diary;
		this.imageFilename = imageFilename;

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
		// Write out the fragments to disk
		// ---------------------------------------------------------------------
		ObjectMapper objectMapper = new ObjectMapper();
		for (Fragment fragment : outputDocument.fragments) {

			if (fragment.html == null) {
				throw new Exception("null html found in fragment: " + fragment);
			}

			String fragmentDirName = fragment.toDirectoryName();

			// ---------------------------------------------------------------------
			// Write out the fragment as a json info file and a separate text file
			// with the html content
			// ---------------------------------------------------------------------
			String fragmentPathName = baseDirName + "/" + fragmentDirName;
			File fragmentDir = new File(fragmentPathName);
			fragmentDir.mkdirs();

			File jsonFile = new File(fragmentDir, "fragment.json");
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, fragment);

			Path htmlPath = new File(fragmentDir, "fragment.html").toPath();
			try (BufferedWriter writer = Files.newBufferedWriter(htmlPath)) {
				writer.write(fragment.html);
			}
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
