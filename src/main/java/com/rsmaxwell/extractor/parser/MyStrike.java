package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyStrike extends MyElement {

	private String strike;

	public static MyStrike create(Element element, int level) throws Exception {
		MyStrike myStrike = new MyStrike();
		return myStrike;
	}

	@Override
	public boolean getStrike() {
		return true;
	}
}
