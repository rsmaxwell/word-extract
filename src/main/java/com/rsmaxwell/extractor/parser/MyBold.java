package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyBold extends MyElement {

	private String bold;

	public static MyBold create(Element element, int level) throws Exception {
		MyBold myBold = new MyBold();
		return myBold;
	}
}
