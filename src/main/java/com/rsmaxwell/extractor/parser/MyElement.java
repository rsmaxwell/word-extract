package com.rsmaxwell.extractor.parser;

public abstract class MyElement implements MyNode {

	protected static final String LS = System.getProperty("line.separator");

	@Override
	public String toString() {
		return "";
	}

	public String toHtml() {
		return "";
	}

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
