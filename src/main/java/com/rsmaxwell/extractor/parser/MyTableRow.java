package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsmaxwell.diaryjson.Day;
import com.rsmaxwell.diaryjson.OutputDocument;
import com.rsmaxwell.diaryjson.fragment.Fragment;
import com.rsmaxwell.extractor.Extractor;

public class MyTableRow extends MyElement {

	private ArrayList<MyTableColumn> columns = new ArrayList<MyTableColumn>();

	public static MyTableRow create(Element element, int level) throws Exception {

		MyTableRow tableRow = new MyTableRow();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:tc".contentEquals(nodeName)) {
					tableRow.columns.add(MyTableColumn.create(childElement, level + 1));
				} else if ("w:tblPrEx".contentEquals(nodeName)) {
					// ok
				} else if ("w:trPr".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return tableRow;
	}

	@Override
	public String toString() {

		switch (columns.size()) {

		// Check the the day-of-the-month
		case 3: // day-of-month
		{
			try {
				int dayOfMonth = columns.get(1).getDayOfMonth();
				return "---[ " + dayOfMonth + " ]------------ " + LS;
			} catch (Exception e) {
			}
			break;
		}

		case 2: // html/text
			// Concatenate the block of text
			StringBuilder sb1 = new StringBuilder();
			for (MyTableColumn column : columns) {
				sb1.append(column.toString());
			}
			String string = sb1.toString();

			StringBuilder sb2 = new StringBuilder();

			// Check the the day-name
			Matcher matcher = Day.getMatcher(string);
			if (matcher.find()) {
				String day = matcher.group(1);
				String text = matcher.group(2);

				sb2.append("---[ " + day + " ]---" + LS);
				sb2.append(text + LS);
			} else {
				// Otherwise it is plain text
				sb2.append(string);
			}

			return sb2.toString();
		}

		return "";
	}

	public void toOutput(OutputDocument outputDocument) throws Exception {

		Extractor extractor = Extractor.instance;

		switch (columns.size()) {

		case 3: {

			try {
				// day-of-month
				extractor.day = columns.get(1).getDayOfMonth();
				Fragment fragment = new Fragment(extractor.year, extractor.month, extractor.day, extractor.order);
				fragment.diary = extractor.diary;
				fragment.imageFilename = extractor.imageFilename;
				fragment.check();
				outputDocument.fragments.add(fragment);

				// day-name
				String line = columns.get(0).toString();
				Matcher matcher = Day.getMatcher(line);
				if (matcher.find()) {
					String string = matcher.group(1);
					int dayOfWeek = Day.toInt(string);
					Day.check(extractor.year, extractor.month, extractor.day, dayOfWeek);
				}
			} catch (Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unexpected day header\n");
				sb.append(
						extractor.year + "-" + extractor.month + "-" + extractor.day + "-" + extractor.order + "   " + extractor.wordFilename + "\n");
				sb.append("columns: { \"" + columns.get(0) + "\", \"" + columns.get(1) + "\", \"" + columns.get(2) + "\" }\n");
				throw new Exception(sb.toString(), e);
			}
			break;
		}

		case 2: // html/text

			// Concatenate the block of lines (from column 0) and notes (from column 1)
			List<String> lines = new ArrayList<String>();
			List<String> notes = new ArrayList<String>();

			lines.addAll(columns.get(0).toHtmlList());
			notes.addAll(columns.get(1).toHtmlList());

			// Check there is at least one line
			if (lines.size() <= 0) {
				return;
			}

			// Add this line to the last fragment
			int size = outputDocument.fragments.size();
			if (size <= 0) {
				throw new Exception("The month does not contain any days");
			}

			Fragment fragment = outputDocument.fragments.get(size - 1);

			try {
				if (fragment.html == null) {
					fragment.html = buildLine(lines);
				} else {
					fragment.html += buildLine(lines);
				}

				if (fragment.notes == null) {
					fragment.notes = buildLine(notes);
				} else {
					fragment.notes += buildLine(notes);
				}
			} catch (Exception e) {
				throw new Exception("fragment: " + fragment.toString(), e);
			}

			break;
		}

	}

	private String buildLine(List<String> array) throws Exception {
		String line = null;
		String separator = " ";
		for (int i = 0; i < array.size(); i++) {
			String next = array.get(i);
			if (next.length() == 0) {
				separator = "</p>" + LS + "<p>";
			} else {
				line = join(line, next, separator);
				separator = " ";
			}
		}

		if (line == null) {
			return null;
		} else if (line.length() == 0) {
			throw new Exception("Empty paragraph");
		} else {
			return "<p>" + line + "</p>" + LS;
		}
	}

	private String join(String one, String two, String separator) throws Exception {
		if (one == null) {
			return two.trim();
		}

		return one.trim() + separator + two.trim();
	}
}
