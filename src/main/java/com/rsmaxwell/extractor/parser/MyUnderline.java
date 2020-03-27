package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyUnderline extends MyElement {

	private String underline;

	public static MyUnderline create(Element element, int level) throws Exception {

		MyUnderline myUnderline = new MyUnderline();

		String value = element.getAttribute("w:val");
		if (value != null) {
			if (value.length() > 0) {
				myUnderline.underline = value;
			}
		}

		return myUnderline;
	}

	@Override
	public String getUnderline() {
		return underline;
	}
}
