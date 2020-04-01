package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyRunProperties extends MyElement {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyRunProperties create(Element element, int level) throws Exception {

		MyRunProperties runProperties = new MyRunProperties();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:highlight".contentEquals(nodeName)) {
					runProperties.elements.add(MyHighlight.create(childElement, level + 1));
				} else if ("w:vertAlign".contentEquals(nodeName)) {
					runProperties.elements.add(MyVerticalAlign.create(childElement, level + 1));
				} else if ("w:rFonts".contentEquals(nodeName)) {
					// ok
				} else if ("w:sz".contentEquals(nodeName)) {
					runProperties.elements.add(MySize.create(childElement, level + 1));
				} else if ("w:szCs".contentEquals(nodeName)) {
					// ok
				} else if ("w:b".contentEquals(nodeName)) {
					runProperties.elements.add(MyBold.create(childElement, level + 1));
				} else if ("w:u".contentEquals(nodeName)) {
					runProperties.elements.add(MyUnderline.create(childElement, level + 1));
				} else if ("w:color".contentEquals(nodeName)) {
					runProperties.elements.add(MyColour.create(childElement, level + 1));
				} else if ("w:shd".contentEquals(nodeName)) {
					// ok
				} else if ("w:bCs".contentEquals(nodeName)) {
					// ok
				} else if ("w:kern".contentEquals(nodeName)) {
					// ok
				} else if ("w:lang".contentEquals(nodeName)) {
					// ok
				} else if ("w:strike".contentEquals(nodeName)) {
					runProperties.elements.add(MyStrike.create(childElement, level + 1));
				} else if ("w:i".contentEquals(nodeName)) {
					runProperties.elements.add(MyItalic.create(childElement, level + 1));
				} else if ("w:iCs".contentEquals(nodeName)) {
					// ok
				} else if ("w:rStyle".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return runProperties;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public String getHighlight() {
		for (MyElement element : elements) {
			String highlight = element.getHighlight();
			if (highlight != null) {
				return highlight;
			}
		}
		return null;
	}

	@Override
	public boolean getBold() {
		for (MyElement element : elements) {
			boolean bold = element.getBold();
			if (bold) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getUnderline() {
		for (MyElement element : elements) {
			String underline = element.getUnderline();
			if (underline != null) {
				return underline;
			}
		}
		return null;
	}

	@Override
	public String getSize() {
		for (MyElement element : elements) {
			String size = element.getSize();
			if (size != null) {
				return size;
			}
		}
		return null;
	}

	@Override
	public String getColour() {
		for (MyElement element : elements) {
			String colour = element.getColour();
			if (colour != null) {
				return colour;
			}
		}
		return null;
	}

	@Override
	public boolean getItalic() {
		for (MyElement element : elements) {
			boolean italic = element.getItalic();
			if (italic) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getStrike() {
		for (MyElement element : elements) {
			boolean strike = element.getStrike();
			if (strike) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getVerticalAlign() {
		for (MyElement element : elements) {
			String alignType = element.getVerticalAlign();
			if (alignType != null) {
				return alignType;
			}
		}
		return null;
	}
}
