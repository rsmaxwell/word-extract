package com.rsmaxwell.extractor.parser;

public abstract class MyElement implements MyNode {

	@Override
	public String toString() {
		return "";
	}

	public String toHTML() {
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
