package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyVerticalAlign extends MyElement {

	private String alignType;

	public static MyVerticalAlign create(Element element, int level) throws Exception {
		MyVerticalAlign myVerticalAlig = new MyVerticalAlign();

		String value = element.getAttribute("w:val");
		if (value != null) {
			if (value.length() > 0) {
				myVerticalAlig.alignType = value;
			}
		}

		return myVerticalAlig;
	}

	@Override
	public String getVerticalAlign() {
		return alignType;
	}
}
