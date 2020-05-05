package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyRunStyle extends MyElement {

	private String style;

	public static MyRunStyle create(Element element, int level) throws Exception {

		MyRunStyle myRunStyle = new MyRunStyle();

		String value = element.getAttribute("w:val");
		if (value != null) {
			if (value.length() > 0) {
				myRunStyle.style = value;
			}
		}

		return myRunStyle;
	}

	@Override
	public String getRunStyle() {
		return style;
	}
}
