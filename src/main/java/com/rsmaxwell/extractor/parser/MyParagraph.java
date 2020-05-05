package com.rsmaxwell.extractor.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyParagraph extends MyElement {

	private List<MyRun> runs = new ArrayList<MyRun>();

	public static MyParagraph create(Element element, int level) throws Exception {

		MyParagraph paragraph = new MyParagraph();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:r".contentEquals(nodeName)) {
					paragraph.runs.add(MyRun.create(childElement, level + 1));
				} else if ("w:pPr".contentEquals(nodeName)) {
					// ok
				} else if ("w:proofErr".contentEquals(nodeName)) {
					// ok
				} else if ("w:bookmarkStart".contentEquals(nodeName)) {
					// ok
				} else if ("w:bookmarkEnd".contentEquals(nodeName)) {
					// ok
				} else if ("w:hyperlink".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return paragraph;
	}

	@Override
	public String toHtml() {

		StringBuilder sb = new StringBuilder();

		for (MyRun run : runs) {
			sb.append(run.toHtml());
		}
		String html = sb.toString().trim();

		List<String> allPictures = new ArrayList<String>();
		for (MyRun run : runs) {
			List<String> pictures = run.getPictures();
			allPictures.addAll(pictures);
		}

		if (allPictures.isEmpty()) {
			return "<p>" + html + "</p>" + LS;
		}
		File file = new File(allPictures.get(0));
		String name = file.getName();
		File parent = file.getParentFile();
		String parentName = parent.getName();
		String image = parentName + "/" + name;

		sb = new StringBuilder();
		sb.append("<figure> + LS");
		sb.append("  <img src=\"" + image + "\" width=\"600px\" /> + LS");
		sb.append("  <figcaption>" + html + "</figcaption> + LS");
		sb.append("</figure> + LS");

		return sb.toString();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (MyRun run : runs) {
			sb.append(run.toString());
		}

		return sb.toString();
	}
}
