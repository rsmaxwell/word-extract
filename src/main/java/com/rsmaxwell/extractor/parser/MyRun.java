package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyRun {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyRun create(Element element, int level) throws Exception {

		MyRun run = new MyRun();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:rPr".contentEquals(nodeName)) {
					run.elements.add(MyRunProperties.create(childElement, level + 1));
				} else if ("w:br".contentEquals(nodeName)) {
					run.elements.add(MyBreak.create(childElement, level + 1));
				} else if ("w:t".contentEquals(nodeName)) {
					run.elements.add(MyText.create(childElement, level + 1));
				} else if ("w:tab".contentEquals(nodeName)) {
					run.elements.add(MyTab.create(childElement, level + 1));
				} else if ("w:lastRenderedPageBreak".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return run;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (MyElement element : elements) {
			sb.append(element.toString());
		}

		String highlight = getHighlight();
		if (highlight == null) {
			return sb.toString();
		} else {
			String colour = toHTMLcolour(highlight);

			if (colour == null) {
				System.out.println("ERROR: unexpected highlight value: " + highlight);
				colour = "?";
			}

			return "<span class=highlight_" + colour + ">" + sb.toString() + "</span>";
		}
	}

	private static Map<String, String> colours = new HashMap<String, String>();

	static {
		colours.put("yellow", "yellow");
		colours.put("green", "lime");
		colours.put("cyan", "aqua");
		colours.put("magenta", "magenta");
		colours.put("blue", "blue");
		colours.put("red", "red");
		colours.put("darkBlue", "navy");
		colours.put("darkCyan", "teal");
		colours.put("darkGreen", "green");
		colours.put("darkMagenta", "purple");
		colours.put("darkRed", "maroon");
		colours.put("darkYellow", "olive");
		colours.put("darkGray", "gray");
		colours.put("lightGray", "silver");
		colours.put("black", "black");
	}

	private String toHTMLcolour(String ms_word) {
		return colours.get(ms_word);
	}

	public String getHighlight() {
		for (MyElement element : elements) {
			String highlight = element.getHighlight();
			if (highlight != null) {
				return highlight;
			}
		}
		return null;
	}
}
