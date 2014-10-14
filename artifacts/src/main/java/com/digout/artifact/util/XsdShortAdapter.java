package com.digout.artifact.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XsdShortAdapter extends XmlAdapter<String, Short> {

	@Override
	public Short unmarshal(String v) throws Exception {
		return Short.valueOf(v);
	}

	@Override
	public String marshal(Short v) throws Exception {
		return String.valueOf(v);
	}

}
