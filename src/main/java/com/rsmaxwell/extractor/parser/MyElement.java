package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;

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

	public List<String> getPictures() {
		return new ArrayList<String>();
	}
}
