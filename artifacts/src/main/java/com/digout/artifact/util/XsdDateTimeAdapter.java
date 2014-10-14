package com.digout.artifact.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XsdDateTimeAdapter extends XmlAdapter<String, DateTime> {

    //private static final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat.dateTime();
	private static final DateTimeFormatter XML_DATE_TIME_FORMAT = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm");

    private static DateTime parseXmlDateTime(String text) {
        try {
            return XML_DATE_TIME_FORMAT.parseDateTime(text);
        }
        catch (IllegalArgumentException ex) {
            //LOGGER.error(text + " can not be parse into DateTime value.", ex);
        	throw ex;
        }
    }

    private static String formatXmlDateTime(DateTime time) {
        return XML_DATE_TIME_FORMAT.print(time);
    }

    @Override
    public DateTime unmarshal(String v) {
        return parseXmlDateTime(v);
    }

    @Override
    public String marshal(DateTime v) {
        return formatXmlDateTime(v);
    }
}