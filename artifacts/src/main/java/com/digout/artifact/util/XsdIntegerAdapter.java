package com.digout.artifact.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

public class XsdIntegerAdapter extends XmlAdapter<String, Integer> {

	@Override
	public Integer unmarshal(String v) throws Exception {
		return Integer.valueOf(v);
	}

	@Override
	public String marshal(Integer v) throws Exception {
		return String.valueOf(v);
	}

}
