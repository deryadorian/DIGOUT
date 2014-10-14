package com.digout.artifact.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XsdDoubleAdapter extends XmlAdapter<String, Double> {

	@Override
	public Double unmarshal(String v) throws Exception {
		return Double.valueOf(v);
	}

	@Override
	public String marshal(Double v) throws Exception {
		return String.valueOf(v);
	}

}
