package com.digout.artifact.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XsdBooleanAdapter extends XmlAdapter<String, Boolean> {

	@Override
	public Boolean unmarshal(String v) throws Exception {
		return Boolean.valueOf(v);
	}

	@Override
	public String marshal(Boolean v) throws Exception {
		return String.valueOf(v);
	}

}
