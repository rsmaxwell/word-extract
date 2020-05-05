package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyInline extends MyElement {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyInline create(Element element, int level) throws Exception {

		MyInline inline = new MyInline();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("wp:extent".contentEquals(nodeName)) {
					// ok
				} else if ("wp:effectExtent".contentEquals(nodeName)) {
					// ok
				} else if ("wp:docPr".contentEquals(nodeName)) {
					inline.elements.add(MyDocProperties.create(childElement, level + 1));
				} else if ("wp:cNvGraphicFramePr".contentEquals(nodeName)) {
					// ok
				} else if ("a:graphic".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return inline;
	}

	@Override
	public List<String> getPictures() {

		List<String> allPictures = new ArrayList<String>();
		for (MyElement element : elements) {
			List<String> picture = element.getPictures();
			allPictures.addAll(picture);
		}

		return allPictures;
	}
}
