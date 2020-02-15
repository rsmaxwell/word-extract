package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyColour extends MyElement {

	private String colour;

	public static MyColour create(Element element, int level) throws Exception {

		MyColour myColour = new MyColour();

		String value = element.getAttribute("w:val");
		if (value != null) {
			if (value.length() > 0) {
				myColour.colour = value;
			}
		}

		return myColour;
	}

	@Override
	public String getColour() {
		return colour;
	}
}
