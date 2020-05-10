package com.rsmaxwell.extractor.relationships;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RelsDocument {

	private List<RelsElement> elements = new ArrayList<RelsElement>();
	// private Map<String, String> relationships = new HashMap<String, String>();

	public static RelsDocument create(Element element, int level) throws Exception {

		RelsDocument document = new RelsDocument();

		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				String nodename = node.getNodeName();
				if ("Relationship".equals(nodename)) {
					document.elements.add(Relationship.create(childElement, level + 1));

				} else {
					System.out.println("element: " + node.getNodeType() + " : " + node.getNodeName());
				}
			} else {
				System.out.println("node: " + node.getNodeType() + " : " + node.getNodeName() + " : " + node.getNodeValue());
			}
		}

		return document;
	}

	public Map<String, String> toMap() throws Exception {
		Map<String, String> relationships = new HashMap<String, String>();

		for (RelsElement element : elements) {
			element.add(relationships);
		}

		return relationships;
	}
}
