package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsmaxwell.extractor.parser.MyElement;

public class MyDrawing extends MyElement {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyDrawing create(Element element, int level) throws Exception {

		MyDrawing drawing = new MyDrawing();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("wp:inline".contentEquals(nodeName)) {
					drawing.elements.add(MyInline.create(childElement, level + 1));
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return drawing;
	}

	@Override
	public List<String> getPictures() {

		List<String> allPictures = new ArrayList<String>();
		for (MyElement element : elements) {
			List<String> pictures = element.getPictures();
			allPictures.addAll(pictures);
		}

		return allPictures;
	}
}
