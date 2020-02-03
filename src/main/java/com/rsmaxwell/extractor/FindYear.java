package com.rsmaxwell.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindYear {

	private static final String docxRegex = "^img([0-9]{4})(-(left|right))?\\.docx$";

	private static Map<String, Integer> sideMap = new HashMap<String, Integer>();

	private static int NONE = 0;
	private static int LEFT = 1;
	private static int RIGHT = 2;

	static {
		sideMap.put(null, NONE);
		sideMap.put("left", LEFT);
		sideMap.put("right", RIGHT);
	}

	public static int get(String string) throws Exception {

		Pattern docxPattern = Pattern.compile(docxRegex);
		Matcher m = docxPattern.matcher(string);

		if (!m.find()) {
			throw new Exception("Unexpected filename: " + string);
		}

		String numberString = m.group(1);
		String sideString = m.group(3);

		int number = Integer.parseInt(numberString);
		int side = sideMap.get(sideString);

		int year = -1;

		if (number < 2833) {
			throw new Exception("number too small: " + number);
		}

		if (number <= 2866) {
			year = 1828;
		} else if (number == 2867) {
			if (side == LEFT) {
				year = 1828;
			} else {
				year = 1829;
			}
		} else if (number <= 2935) {
			year = 1829;
		} else if (number == 2936) {
			if (side == LEFT) {
				year = 1829;
			} else {
				year = 1830;
			}
		} else if (number <= 2939) {
			year = 1830;
		} else {
			throw new Exception("number too large: " + number);
		}

		return year;
	}

	public static void main(String[] args) throws Exception {

		List<Testcase> list = new ArrayList<Testcase>();
		list.add(new Testcase("img2867-left.docx", 1828));
		list.add(new Testcase("img2867-right.docx", 1829));
		list.add(new Testcase("img2939.docx", 1830));

		for (Testcase test : list) {
			int year = get(test.filename);

			if (year != test.year) {
				throw new Exception("ERROR: expected: " + test.year + ", actual: " + year);
			}
		}

		System.out.println("Success");
	}
}
