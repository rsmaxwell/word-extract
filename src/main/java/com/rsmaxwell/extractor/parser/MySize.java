package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MySize extends MyElement {

	private String size;

	public static MySize create(Element element, int level) throws Exception {

		MySize myHighlight = new MySize();

		String value = element.getAttribute("w:val");
		if (value != null) {
			if (value.length() > 0) {
				myHighlight.size = value;
			}
		}

		return myHighlight;
	}

	@Override
	public String getSize() {
		return size;
	}
}
