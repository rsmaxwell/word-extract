package com.rsmaxwell.extract.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsmaxwell.extract.output.OutputDay;
import com.rsmaxwell.extract.output.OutputMonth;

public class MyTableRow {

	static final String[] daynames = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

	static Pattern pattern;

	static {
		StringBuilder sb = new StringBuilder();
		String separator = "";
		for (String day : daynames) {
			sb.append(separator);
			sb.append(day);
			separator = "|";
		}
		String regx = "^(" + sb.toString() + ")\\s+(.*)";
		pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

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

		String eol = System.getProperty("line.separator");

		// Check the the day-of-the-month
		if ((columns.size() == 3) && (columns.get(0).isDivider()) && (columns.get(1).isNumber())
				&& (columns.get(2).isDivider())) {
			return "---[ " + columns.get(1).toString() + " ]------------ " + eol;
		}

		// Concatenate the block of text
		StringBuilder sb1 = new StringBuilder();
		for (MyTableColumn column : columns) {
			sb1.append(column.toString());
		}
		String string = sb1.toString();

		StringBuilder sb2 = new StringBuilder();

		// Check the the day-name
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			String day = matcher.group(1);
			String text = matcher.group(2);

			sb2.append("---[ " + day + " ]---" + eol);
			sb2.append(text + eol);
		} else {
			// Otherwise it is plain text
			sb2.append(string);
		}

		return sb2.toString();
	}

	public void toOutput(OutputMonth outputMonth) throws Exception {

		// Check the the day-of-month
		if ((columns.size() == 3) && (columns.get(0).isDivider()) && (columns.get(1).isNumber())
				&& (columns.get(2).isDivider())) {

			String string = columns.get(1).toString();
			int day = Integer.parseInt(string);

			OutputDay outputDay = new OutputDay();
			outputDay.day = day;

			outputMonth.days.add(outputDay);

			return;
		}

		// Check there is at least one day in the given outputMonth
		int size = outputMonth.days.size();
		if (size <= 0) {
			throw new Exception("The month does not contain any days");
		}

		// Check there are exactly 2 columns in this row
		if (columns.size() != 2) {
			return;
		}

		// Concatenate the block of lines (from column 0) and notes (from column 1)
		List<String> lines = new ArrayList<String>();
		List<String> notes = new ArrayList<String>();

		lines.addAll(columns.get(0).toList());
		notes.addAll(columns.get(1).toList());

		// Check there is at least one line
		if (lines.size() <= 0) {
			return;
		}

		// If the first line starts with the day-name, remove it
		String line = lines.get(0);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			lines.set(0, matcher.group(2));
		}

		OutputDay outputDay = outputMonth.days.get(size - 1);
		outputDay.lines.addAll(lines);
		outputDay.notes.addAll(notes);
	}

}
