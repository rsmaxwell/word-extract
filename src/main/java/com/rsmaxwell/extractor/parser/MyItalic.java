package com.rsmaxwell.extractor.parser;

import org.w3c.dom.Element;

public class MyItalic extends MyElement {

	public static MyItalic create(Element element, int level) throws Exception {
		MyItalic myItalic = new MyItalic();
		return myItalic;
	}

	@Override
	public boolean getItalic() {
		return true;
	}
}
