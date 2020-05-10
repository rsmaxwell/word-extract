package com.rsmaxwell.extractor.parser;

public abstract class MyElement implements MyNode {

	protected static final String LS = System.getProperty("line.separator");

	@Override
	public String toString() {
		return "";
	}

	public Html toHtml() {
		return new Html("", 0);
	}

	public String getHighlight() {
		return null;
	}

	public String getRunStyle() {
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

	public String getPicture() {
		return null;
	}

	public String getHyperlinkId() {
		return null;
	}
}
