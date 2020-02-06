package com.rsmaxwell.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindYear {

	private static final String docxRegex = "^img([0-9]{4})(-(left|right))?\\.docx$";

	private static int NONE = 0;
	private static int LEFT = 1;
	private static int RIGHT = 2;

	private static Map<String, Integer> sideMap = new HashMap<String, Integer>();

	static {
		sideMap.put(null, NONE);
		sideMap.put("left", LEFT);
		sideMap.put("right", RIGHT);
	}

	public static int get(String filename) throws Exception {

		File file = new File(filename);
		String basename = file.getName();

		Pattern docxPattern = Pattern.compile(docxRegex);
		Matcher m = docxPattern.matcher(basename);

		if (!m.find()) {
			throw new Exception("Unexpected filename: " + filename);
		}

		String numberString = m.group(1);
		String sideString = m.group(3);

		int number = Integer.parseInt(numberString);
		int side = sideMap.get(sideString);

		int year = -1;

		if (number < 2348) {
			throw new Exception("number too small: " + number);
		}

		// ---[ diary-1830 ]---------------------
		// list.add(new Testcase("img2349.docx", 1830));
		// list.add(new Testcase("img2427.docx", 1830));

		else if (number <= 2427) {
			year = 1830;
		}

		// ---[ diary-1838 ]---------------------
		// list.add(new Testcase("img2430.docx", 1838));
		// list.add(new Testcase("img2484.docx", 1838));

		else if (number <= 2484) {
			year = 1838;
		}

		// ---[ diary-1839 ]---------------------
		// list.add(new Testcase("img2485.docx", 1839));
		// list.add(new Testcase("img2552.docx", 1839));

		else if (number <= 2552) {
			year = 1839;
		}

		// ---[ diary-1837 ]---------------------
		// list.add(new Testcase("img2553.docx", 1837));
		// list.add(new Testcase("img2601.docx", 1837));

		else if (number <= 2601) {
			year = 1837;
		}

		// ---[ diary-1836 ]---------------------
		// list.add(new Testcase("img2602.docx", 1836));
		// list.add(new Testcase("img2647.docx", 1836));

		else if (number <= 2647) {
			year = 1836;
		}

		// ---[ diary-1835 ]---------------------
		// list.add(new Testcase("img2648.docx", 1835));
		// list.add(new Testcase("img2692.docx", 1835));

		else if (number <= 2692) {
			year = 1835;
		}

		// ---[ diary-1834 ]---------------------
		// list.add(new Testcase("img2693.docx", 1834));
		// list.add(new Testcase("img2732.docx", 1834));

		else if (number <= 2732) {
			year = 1834;
		}

		// ---[ diary-1831 ]---------------------
		// list.add(new Testcase("img2733.docx", 1831));
		// list.add(new Testcase("img2831.docx", 1831));

		else if (number <= 2831) {
			year = 1831;
		}

		// ---[ diary-1828-and-1829-and-jan-1830 ]---------------------
		// list.add(new Testcase("img2833-right.docx", 1828));
		// list.add(new Testcase("img2867-left.docx", 1828));

		else if (number <= 2866) {
			year = 1828;
		} else if (number == 2867) {
			if (side == LEFT) {
				year = 1828;
			} else {
				year = 1829;
			}
		}

		// list.add(new Testcase("img2867-right.docx", 1829));
		// list.add(new Testcase("img2936-left.docx", 1829));

		else if (number <= 2935) {
			year = 1829;
		} else if (number == 2936) {
			if (side == LEFT) {
				year = 1829;
			} else {
				year = 1830;
			}
		}

		// list.add(new Testcase("img2936-right.docx", 1830));
		// list.add(new Testcase("img2939-right.docx", 1830));

		else if (number <= 2939) {
			year = 1830;
		}

		// ---[ diary-1832 ]---------------------
		// list.add(new Testcase("img2982.docx", 1832));
		// list.add(new Testcase("img3077.docx", 1832));

		else if (number <= 3077) {
			year = 1832;
		} else {
			throw new Exception("number too large: " + number);
		}

		return year;
	}

	public static void main(String[] args) throws Exception {

		List<Testcase> list = new ArrayList<Testcase>();

		// ---[ diary-1828-and-1829-and-jan-1830 ]---------------------
		list.add(new Testcase("img2833-right.docx", 1828));
		list.add(new Testcase("img2867-left.docx", 1828));

		list.add(new Testcase("img2867-right.docx", 1829));
		list.add(new Testcase("img2936-left.docx", 1829));

		list.add(new Testcase("img2936-right.docx", 1830));
		list.add(new Testcase("img2939-right.docx", 1830));

		// ---[ diary-1830 ]---------------------
		list.add(new Testcase("img2349.docx", 1830));
		list.add(new Testcase("img2427.docx", 1830));

		// ---[ diary-1831 ]---------------------
		list.add(new Testcase("img2733.docx", 1831));
		list.add(new Testcase("img2831.docx", 1831));

		// ---[ diary-1832 ]---------------------
		list.add(new Testcase("img2982.docx", 1832));
		list.add(new Testcase("img3077.docx", 1832));

		// ---[ diary-1834 ]---------------------
		list.add(new Testcase("img2693.docx", 1834));
		list.add(new Testcase("img2732.docx", 1834));

		// ---[ diary-1835 ]---------------------
		list.add(new Testcase("img2648.docx", 1835));
		list.add(new Testcase("img2692.docx", 1835));

		// ---[ diary-1836 ]---------------------
		list.add(new Testcase("img2602.docx", 1836));
		list.add(new Testcase("img2647.docx", 1836));

		// ---[ diary-1837 ]---------------------
		list.add(new Testcase("img2553.docx", 1837));
		list.add(new Testcase("img2601.docx", 1837));

		// ---[ diary-1838 ]---------------------
		list.add(new Testcase("img2430.docx", 1838));
		list.add(new Testcase("img2484.docx", 1838));

		// ---[ diary-1839 ]---------------------
		list.add(new Testcase("img2485.docx", 1839));
		list.add(new Testcase("img2552.docx", 1839));

		for (Testcase test : list) {
			int year = get(test.filename);

			if (year != test.year) {
				throw new Exception("ERROR: [" + test.filename + "]: expected: " + test.year + ", actual: " + year);
			}
		}

		System.out.println("Success");
	}
}
