package com.rsmaxwell.extract.output;

import java.util.ArrayList;
import java.util.List;

public class OutputMonth {

	public int month;
	public List<OutputDay> days = new ArrayList<OutputDay>();

	public OutputMonth(int value) {
		month = value;
	}
}
