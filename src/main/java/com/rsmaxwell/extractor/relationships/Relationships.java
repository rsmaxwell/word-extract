package com.rsmaxwell.extractor.relationships;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Relationships {

	private Map<String, String> map = new HashMap<String, String>();

	public Relationships(File documentDir) throws IOException, Exception {

		File documentWordDir = new File(documentDir, "word");
		File wordRelsDir = new File(documentWordDir, "_rels");
		File wordRelsFile = new File(wordRelsDir, "document.xml.rels");

		if (!wordRelsFile.exists()) {
			throw new Exception("wordRelsFile not found: " + wordRelsFile.getCanonicalPath());
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(wordRelsFile);

		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();

		RelsDocument document = RelsDocument.create(root, 0);
		map = document.toMap();
	}

	public String get(String id) {
		return map.get(id);
	}
}
