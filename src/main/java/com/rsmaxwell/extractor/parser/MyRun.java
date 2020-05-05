package com.rsmaxwell.extractor.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyRun extends MyElement {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyRun create(Element element, int level) throws Exception {

		MyRun run = new MyRun();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:rPr".contentEquals(nodeName)) {
					run.elements.add(MyRunProperties.create(childElement, level + 1));
				} else if ("w:br".contentEquals(nodeName)) {
					run.elements.add(MyBreak.create(childElement, level + 1));
				} else if ("w:t".contentEquals(nodeName)) {
					run.elements.add(MyText.create(childElement, level + 1));
				} else if ("w:tab".contentEquals(nodeName)) {
					run.elements.add(MyTab.create(childElement, level + 1));
				} else if ("w:lastRenderedPageBreak".contentEquals(nodeName)) {
					// ok
				} else if ("w:drawing".contentEquals(nodeName)) {
					run.elements.add(MyDrawing.create(childElement, level + 1));
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return run;
	}

	@Override
	public List<String> getPictures() {

		List<String> allPictures = new ArrayList<String>();
		for (MyElement element : elements) {
			List<String> picture = element.getPictures();
			allPictures.addAll(picture);
		}

		return allPictures;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (MyElement element : elements) {
			sb.append(element.toString());
		}

		return sb.toString();
	}

	@Override
	public String toHtml() {

		StringBuilder sb = new StringBuilder();

		// --------------------------------------------
		// Get the text string for this run
		// --------------------------------------------
		for (MyElement element : elements) {
			sb.append(element.toString());
		}
		String text = sb.toString();

		// --------------------------------------------
		// Wrap the text with a highlight span
		// --------------------------------------------
		String highlight = getHighlight();
		if (highlight != null) {
			String highlight2 = wordColourNameToHTML(highlight);

			if (highlight2 == null) {
				throw new RuntimeException("unexpected highlight value: " + highlight);
			}

			text = "<span class=highlight_" + highlight2 + ">" + text + "</span>";
		}

		// --------------------------------------------
		// Wrap the text with a highlight span
		// --------------------------------------------
		String runStyle = getRunStyle();
		if (runStyle != null) {
			String colour = runStyleToHighlightColour(runStyle);

			if (colour == null) {
				throw new RuntimeException("unexpected runStyle value: " + runStyle);
			}

			text = "<span class=highlight_" + colour + ">" + text + "</span>";
		}

		// --------------------------------------------
		// Wrap the text with a bold span
		// --------------------------------------------
		boolean bold = getBold();
		if (bold) {
			text = "<b>" + text + "</b>";
		}

		// --------------------------------------------
		// Wrap the text with an underline span
		// --------------------------------------------
		String underline = getUnderline();
		if (underline != null) {
			if ("single".equals(underline)) {
				text = "<u>" + text + "</u>";
			} else if ("double".equals(underline)) {
				text = "<span class=underline_double>" + text + "</span>";
			} else {
				throw new RuntimeException("unexpected underline value: " + underline);
			}
		}

		// --------------------------------------------
		// Wrap the text with a font tag
		// --------------------------------------------
		String size = getSize();
		String colour = getColour();

		if ((size != null) || (colour != null)) {
			String separator = "";
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<font ");

			if (size != null) {
				int wordFontSize = Integer.parseInt(size);
				if (wordFontSize != 24) {

					double wordFontSize2 = wordFontSize;
					double point = wordFontSize2 / 8.0;
					String pointString = String.format("%.1f", point);

					sb2.append(separator + "size=" + pointString + "pt");
					separator = " ";
				}
			}

			if (colour != null) {
				sb2.append(separator + "color=#" + colour);
				separator = " ";
			}

			sb2.append(">");
			sb2.append(text);
			sb2.append("</font>");
			text = sb2.toString();
		}

		// --------------------------------------------
		// Wrap the text with a italic span
		// --------------------------------------------
		boolean italic = getItalic();
		if (italic) {
			text = "<i>" + text + "</i>";
		}

		// --------------------------------------------
		// Wrap the text with a strike span
		// --------------------------------------------
		boolean strike = getStrike();
		if (strike) {
			text = "<del>" + text + "</del>";
		}

		// --------------------------------------------
		// Wrap the text with a superscript span
		// --------------------------------------------
		String alignType = getVerticalAlign();
		if (alignType != null) {
			if ("superscript".equals(alignType)) {
				text = "<sup>" + text + "</sup>";
			} else if ("subscript".equals(alignType)) {
				text = "<sub>" + text + "</sub>";
			} else {
				throw new RuntimeException("unexpected VerticalAlign value: " + alignType);
			}
		}

		// --------------------------------------------
		// Return the text string
		// --------------------------------------------
		return text;
	}

	private static Map<String, String> colours = new HashMap<String, String>();

	static {
		colours.put("yellow", "yellow");
		colours.put("green", "lime");
		colours.put("cyan", "aqua");
		colours.put("magenta", "magenta");
		colours.put("blue", "blue");
		colours.put("red", "red");
		colours.put("darkBlue", "navy");
		colours.put("darkCyan", "teal");
		colours.put("darkGreen", "green");
		colours.put("darkMagenta", "purple");
		colours.put("darkRed", "maroon");
		colours.put("darkYellow", "olive");
		colours.put("darkGray", "gray");
		colours.put("lightGray", "silver");
		colours.put("black", "black");
	}

	private String wordColourNameToHTML(String ms_word) {
		return colours.get(ms_word);
	}

	private static Map<String, String> runStyleToColourMap = new HashMap<String, String>();
	static {
		runStyleToColourMap.put("highlightyellow1", "yellow");
		runStyleToColourMap.put("highlightgreen1", "lime");
		runStyleToColourMap.put("highlightcyan1", "aqua");
		runStyleToColourMap.put("highlightmagenta1", "magenta");
		runStyleToColourMap.put("highlightblue1", "blue");
		runStyleToColourMap.put("highlightred1", "red");
		runStyleToColourMap.put("highlightdarkBlue1", "navy");
		runStyleToColourMap.put("highlightdarkCyan1", "teal");
		runStyleToColourMap.put("highlightdarkGreen1", "green");
		runStyleToColourMap.put("highlightdarkMagenta1", "purple");
		runStyleToColourMap.put("highlightdarkRed1", "maroon");
		runStyleToColourMap.put("highlightdarkYellow1", "olive");
		runStyleToColourMap.put("highlightdarkGray1", "gray");
		runStyleToColourMap.put("highlightlightGray1", "silver");
		runStyleToColourMap.put("highlightblack1", "black");
	}

	private String runStyleToHighlightColour(String style) {
		return runStyleToColourMap.get(style);
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
	public String getRunStyle() {
		for (MyElement element : elements) {
			String highlight = element.getRunStyle();
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
