package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyTableColumn {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyTableColumn create(Element element, int level) throws Exception {

		MyTableColumn tableColumn = new MyTableColumn();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();

				if ("w:tcPr".contentEquals(nodeName)) {
					// ok
				} else if ("w:p".contentEquals(nodeName)) {
					tableColumn.elements.add(MyParagraph.create(childElement, level + 1));
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return tableColumn;
	}

	public int getDayOfMonth() throws Exception {

		String string = toString();

		String pattern = "([^\\d]*)(\\d+)([^\\d]*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(string);

		if (!m.find()) {
			throw new Exception("not a day-of-week: " + string);
		}

		String strNum = m.group(2);
		return Integer.parseInt(strNum);
	}

	@Override
	public String toString() {

		String separator = "";
		StringBuilder sb = new StringBuilder();
		for (MyElement element : elements) {

			if (element instanceof MyParagraph) {
				sb.append(separator);
				separator = System.getProperty("line.separator");
			}
			sb.append(element.toString());
		}

		return sb.toString();
	}

	public List<String> toHtmlList() {

		List<String> lines = new ArrayList<String>();

		int paragraphCount = 0;
		StringBuilder sb = new StringBuilder();
		for (MyElement element : elements) {

			if (element instanceof MyParagraph) {
				if (paragraphCount > 0) {
					lines.add(sb.toString());
					sb.setLength(0);
				}
				paragraphCount++;
			}
			sb.append(element.toHtml());
		}
		if (sb.length() > 0) {
			lines.add(sb.toString());
		}

		return lines;
	}
}
