package com.digout.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public final class XMLParser {

    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static final DocumentBuilder documentBuilder = initDocumentBuilder();
    private static final XPathFactory xPathFactory = XPathFactory.newInstance();
    private static final XPath xPath = xPathFactory.newXPath();

    public static String getNodeValue(final String xml, final String xPathExpression) {
        String value = "";
        try {
            Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
            value = (String) xPath.compile(xPathExpression).evaluate(document, XPathConstants.STRING);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return value;
    }

    /*
     * public static boolean equalStringValues(String bankResponseXml, String xPathExpression, String value){ try {
     * Document document = documentBuilder.parse(new ByteArrayInputStream(bankResponseXml.getBytes())); String s =
     * (String) xPath.compile(xPathExpression).evaluate(document, XPathConstants.STRING); System.out.println(s); return
     * !s.equals(value); } catch (SAXException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace();
     * } catch (XPathExpressionException e) { e.printStackTrace(); } return false; }
     */

    private static DocumentBuilder initDocumentBuilder() {
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return builder;
    }

    private XMLParser() {
    }
}
