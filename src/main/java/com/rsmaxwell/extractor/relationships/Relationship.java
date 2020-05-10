package com.rsmaxwell.extractor.relationships;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

public class Relationship extends RelsElement {

	private List<RelsElement> elements = new ArrayList<RelsElement>();
	private String id;
	private String target;

	public static Relationship create(Element element, int level) throws Exception {
		String id = element.getAttribute("Id");
		String type = element.getAttribute("Type");
		String target = element.getAttribute("Target");
		return new Relationship(id, type, target);
	}

	public Relationship(String id, String type, String target) {
		this.id = id;
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public String getId() {
		return id;
	}

	@Override
	void add(Map<String, String> relationships) {
		relationships.put(id, target);
	}
}
