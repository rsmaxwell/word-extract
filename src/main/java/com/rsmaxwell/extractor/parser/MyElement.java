package com.rsmaxwell.extractor.parser;

public abstract class MyElement implements MyNode {

	public String getHighlight() {
		return null;
	}

	public boolean getBold() {
		return false;
	}

	public String getUnderline() {
		return null;
	}

	public String getSize() {
		return null;
	}

	public String getColour() {
		return null;
	}

	public boolean getItalic() {
		return false;
	}

	public boolean getStrike() {
		return false;
	}

	public String getVerticalAlign() {
		return null;
	}
}
