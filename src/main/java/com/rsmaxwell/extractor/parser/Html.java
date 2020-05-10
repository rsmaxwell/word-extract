package com.rsmaxwell.extractor.parser;

public class Html {

	private String html;
	private int length;

	public Html(String html, int length) {
		this.html = html;
		this.length = length;
	}

	public String getHtml() {
		return html;
	}

	public int getLength() {
		return length;
	}

	@Override
	public String toString() {
		return html;
	}
}
