package com.digout.artifact.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XsdLongAdapter extends XmlAdapter<String, Long>{

	@Override
	public Long unmarshal(String v) throws Exception {
		return Long.valueOf(v);
	}

	@Override
	public String marshal(Long v) throws Exception {
		return String.valueOf(v);
	}

}
