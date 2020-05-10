package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsmaxwell.diaryjson.Month;
import com.rsmaxwell.diaryjson.OutputDocument;
import com.rsmaxwell.extractor.Extractor;

public class MyBody {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyBody create(Element element, int level) throws Exception {

		MyBody body = new MyBody();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:p".contentEquals(nodeName)) {
					body.elements.add(MyParagraph.create(childElement, level + 1));
				} else if ("w:tbl".contentEquals(nodeName)) {
					body.elements.add(MyTable.create(childElement, level + 1));
				} else if ("w:sectPr".contentEquals(nodeName)) {
					// ok
				} else if ("w:bookmarkEnd".contentEquals(nodeName)) {
					// ok
				} else if ("w:bookmarkStart".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return body;
	}

	@Override
	public String toString() {

		String eol = System.getProperty("line.separator");

		StringBuilder sb = new StringBuilder();

		for (MyElement element : elements) {

			if (element instanceof MyParagraph) {

				String string = element.toString();
				try {
					Extractor extractor = Extractor.instance;
					extractor.month = Month.toInt(string);

					sb.append("---[ ");
					sb.append(string);
					sb.append(" ]------------------------------------------------------------------------");
					sb.append(eol);
				} catch (Exception e) {
				}

			} else {
				sb.append(element.toString());
			}
		}

		return sb.toString();
	}

	private static String yearMonthPatternString = "([\\d]{4})[\\s]+([a-zA-Z]{3,9})";
	private static Pattern yearMonthPattern = Pattern.compile(yearMonthPatternString);

	public void toOutput(OutputDocument outputDocument) throws Exception {

		for (MyElement element : elements) {

			if (element instanceof MyParagraph) {

				String text = element.toString();
				Matcher yearMonthMatcher = yearMonthPattern.matcher(text);
				boolean matches = yearMonthMatcher.matches();

				if (matches) {
					Extractor extractor = Extractor.instance;

					String yearString = yearMonthMatcher.group(1);
					int year = Integer.parseInt(yearString);

					if (extractor.year != year) {
						throw new Exception("Unexpected year: " + year);
					}

					String monthString = yearMonthMatcher.group(2);
					extractor.month = Month.toInt(monthString);
				}

			} else if (element instanceof MyTable) {
				MyTable myTable = (MyTable) element;
				myTable.toOutput(outputDocument);
			} else {
				throw new Exception("Unexpected element type: " + element.getClass().getSimpleName());
			}
		}

	}
}
