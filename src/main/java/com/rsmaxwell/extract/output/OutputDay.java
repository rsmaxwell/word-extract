package com.rsmaxwell.extract.output;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class OutputDay {

	public int day;

	public List<String> lines = new ArrayList<String>();

	@JsonInclude(Include.NON_EMPTY)
	public List<String> notes = new ArrayList<String>();

}
