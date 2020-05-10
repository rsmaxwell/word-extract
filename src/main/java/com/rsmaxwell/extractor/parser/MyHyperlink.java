package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyHyperlink extends MyElement {

	private String id;
	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyHyperlink create(Element element, int level) throws Exception {

		String id = element.getAttribute("r:id");
		MyHyperlink hyperlink = new MyHyperlink(id);

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:r".contentEquals(nodeName)) {
					hyperlink.elements.add(MyRun.create(childElement, level + 1));
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return hyperlink;
	}

	public MyHyperlink(String id) {
		this.id = id;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (MyElement element : elements) {
			sb.append(element.toString());
		}

		return sb.toString();
	}

	@Override
	public Html toHtml() {

		StringBuilder sb = new StringBuilder();

		// --------------------------------------------
		// Get the text string for this run
		// --------------------------------------------
		for (MyElement element : elements) {
			sb.append(element.toString());
		}
		String text = sb.toString();

		// --------------------------------------------
		// Wrap the text with a highlight span
		// --------------------------------------------
		return new Html("<figcaption>" + text + "</figcaption>", text.length());
	}

	@Override
	public String getRunStyle() {
		for (MyElement element : elements) {
			String highlight = element.getRunStyle();
			if (highlight != null) {
				return highlight;
			}
		}
		return null;
	}

	@Override
	public String getPicture() {

		for (MyElement element : elements) {
			String picture = element.getPicture();
			if (picture != null) {
				return picture;
			}
		}

		return null;
	}

	@Override
	public String getHyperlinkId() {
		return id;
	}
}
