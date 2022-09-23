// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.xml;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.log4j.Category;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import java.util.Hashtable;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import java.io.IOException;
import java.util.Collection;
import java.io.LineNumberReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.ProgressMonitorInputStream;
import java.util.zip.ZipInputStream;
import java.util.Vector;
import java.net.URL;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.xml.sax.ErrorHandler;
import org.apache.log4j.xml.SAXErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.awt.Component;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import org.apache.log4j.receivers.spi.Decoder;

public class XMLDecoder implements Decoder
{
    private static final String ENCODING = "UTF-8";
    private static final String BEGINPART = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><!DOCTYPE log4j:eventSet SYSTEM \"http://localhost/log4j.dtd\"><log4j:eventSet version=\"1.2\" xmlns:log4j=\"http://jakarta.apache.org/log4j/\">";
    private static final String ENDPART = "</log4j:eventSet>";
    private static final String RECORD_END = "</log4j:event>";
    private DocumentBuilder docBuilder;
    private Map additionalProperties;
    private String partialEvent;
    private Component owner;
    
    public XMLDecoder(final Component o) {
        this();
        this.owner = o;
    }
    
    public XMLDecoder() {
        this.additionalProperties = new HashMap();
        this.owner = null;
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try {
            (this.docBuilder = dbf.newDocumentBuilder()).setErrorHandler(new SAXErrorHandler());
            this.docBuilder.setEntityResolver(new Log4jEntityResolver());
        }
        catch (ParserConfigurationException pce) {
            System.err.println("Unable to get document builder");
        }
    }
    
    public void setAdditionalProperties(final Map properties) {
        this.additionalProperties = properties;
    }
    
    private Document parse(final String data) {
        if (this.docBuilder == null || data == null) {
            return null;
        }
        Document document = null;
        try {
            final StringBuffer buf = new StringBuffer(1024);
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><!DOCTYPE log4j:eventSet SYSTEM \"http://localhost/log4j.dtd\"><log4j:eventSet version=\"1.2\" xmlns:log4j=\"http://jakarta.apache.org/log4j/\">");
            buf.append(data);
            buf.append("</log4j:eventSet>");
            final InputSource inputSource = new InputSource(new StringReader(buf.toString()));
            document = this.docBuilder.parse(inputSource);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
    
    public Vector decode(final URL url) throws IOException {
        final boolean isZipFile = url.getPath().toLowerCase().endsWith(".zip");
        InputStream inputStream;
        if (isZipFile) {
            inputStream = new ZipInputStream(url.openStream());
            ((ZipInputStream)inputStream).getNextEntry();
        }
        else {
            inputStream = url.openStream();
        }
        LineNumberReader reader;
        if (this.owner != null) {
            reader = new LineNumberReader(new InputStreamReader(new ProgressMonitorInputStream(this.owner, "Loading " + url, inputStream), "UTF-8"));
        }
        else {
            reader = new LineNumberReader(new InputStreamReader(inputStream, "UTF-8"));
        }
        final Vector v = new Vector();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                final StringBuffer buffer = new StringBuffer(line);
                for (int i = 0; i < 1000; ++i) {
                    buffer.append(reader.readLine()).append("\n");
                }
                final Vector events = this.decodeEvents(buffer.toString());
                if (events != null) {
                    v.addAll(events);
                }
            }
        }
        finally {
            this.partialEvent = null;
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return v;
    }
    
    public Vector decodeEvents(final String document) {
        if (document == null) {
            return null;
        }
        if (document.trim().equals("")) {
            return null;
        }
        String newDoc = null;
        String newPartialEvent = null;
        if (document.lastIndexOf("</log4j:event>") == -1) {
            this.partialEvent += document;
            return null;
        }
        if (document.lastIndexOf("</log4j:event>") + "</log4j:event>".length() < document.length()) {
            newDoc = document.substring(0, document.lastIndexOf("</log4j:event>") + "</log4j:event>".length());
            newPartialEvent = document.substring(document.lastIndexOf("</log4j:event>") + "</log4j:event>".length());
        }
        else {
            newDoc = document;
        }
        if (this.partialEvent != null) {
            newDoc = this.partialEvent + newDoc;
        }
        this.partialEvent = newPartialEvent;
        final Document doc = this.parse(newDoc);
        if (doc == null) {
            return null;
        }
        return this.decodeEvents(doc);
    }
    
    public LoggingEvent decode(final String data) {
        final Document document = this.parse(data);
        if (document == null) {
            return null;
        }
        final Vector events = this.decodeEvents(document);
        if (events.size() > 0) {
            return events.firstElement();
        }
        return null;
    }
    
    private Vector decodeEvents(final Document document) {
        final Vector events = new Vector();
        Object message = null;
        String ndc = null;
        String[] exception = null;
        String className = null;
        String methodName = null;
        String fileName = null;
        String lineNumber = null;
        Hashtable properties = null;
        final NodeList nl = document.getElementsByTagName("log4j:eventSet");
        final Node eventSet = nl.item(0);
        final NodeList eventList = eventSet.getChildNodes();
        for (int eventIndex = 0; eventIndex < eventList.getLength(); ++eventIndex) {
            final Node eventNode = eventList.item(eventIndex);
            if (eventNode.getNodeType() == 1) {
                final Logger logger = Logger.getLogger(eventNode.getAttributes().getNamedItem("logger").getNodeValue());
                final long timeStamp = Long.parseLong(eventNode.getAttributes().getNamedItem("timestamp").getNodeValue());
                final Level level = Level.toLevel(eventNode.getAttributes().getNamedItem("level").getNodeValue());
                final String threadName = eventNode.getAttributes().getNamedItem("thread").getNodeValue();
                final NodeList list = eventNode.getChildNodes();
                final int listLength = list.getLength();
                if (listLength != 0) {
                    for (int y = 0; y < listLength; ++y) {
                        final String tagName = list.item(y).getNodeName();
                        if (tagName.equalsIgnoreCase("log4j:message")) {
                            message = this.getCData(list.item(y));
                        }
                        if (tagName.equalsIgnoreCase("log4j:NDC")) {
                            ndc = this.getCData(list.item(y));
                        }
                        if (tagName.equalsIgnoreCase("log4j:MDC")) {
                            properties = new Hashtable();
                            final NodeList propertyList = list.item(y).getChildNodes();
                            for (int propertyLength = propertyList.getLength(), i = 0; i < propertyLength; ++i) {
                                final String propertyTag = propertyList.item(i).getNodeName();
                                if (propertyTag.equalsIgnoreCase("log4j:data")) {
                                    final Node property = propertyList.item(i);
                                    final String name = property.getAttributes().getNamedItem("name").getNodeValue();
                                    final String value = property.getAttributes().getNamedItem("value").getNodeValue();
                                    properties.put(name, value);
                                }
                            }
                        }
                        if (tagName.equalsIgnoreCase("log4j:throwable")) {
                            final String exceptionString = this.getCData(list.item(y));
                            if (exceptionString != null && !exceptionString.trim().equals("")) {
                                exception = new String[] { exceptionString.trim() };
                            }
                        }
                        if (tagName.equalsIgnoreCase("log4j:locationinfo")) {
                            className = list.item(y).getAttributes().getNamedItem("class").getNodeValue();
                            methodName = list.item(y).getAttributes().getNamedItem("method").getNodeValue();
                            fileName = list.item(y).getAttributes().getNamedItem("file").getNodeValue();
                            lineNumber = list.item(y).getAttributes().getNamedItem("line").getNodeValue();
                        }
                        if (tagName.equalsIgnoreCase("log4j:properties")) {
                            if (properties == null) {
                                properties = new Hashtable();
                            }
                            final NodeList propertyList = list.item(y).getChildNodes();
                            for (int propertyLength = propertyList.getLength(), i = 0; i < propertyLength; ++i) {
                                final String propertyTag = propertyList.item(i).getNodeName();
                                if (propertyTag.equalsIgnoreCase("log4j:data")) {
                                    final Node property = propertyList.item(i);
                                    final String name = property.getAttributes().getNamedItem("name").getNodeValue();
                                    final String value = property.getAttributes().getNamedItem("value").getNodeValue();
                                    properties.put(name, value);
                                }
                            }
                        }
                        if (this.additionalProperties.size() > 0) {
                            if (properties == null) {
                                properties = new Hashtable(this.additionalProperties);
                            }
                            for (final Map.Entry e : this.additionalProperties.entrySet()) {
                                properties.put(e.getKey(), e.getValue());
                            }
                        }
                    }
                    LocationInfo info;
                    if (fileName != null || className != null || methodName != null || lineNumber != null) {
                        info = new LocationInfo(fileName, className, methodName, lineNumber);
                    }
                    else {
                        info = LocationInfo.NA_LOCATION_INFO;
                    }
                    ThrowableInformation throwableInfo = null;
                    if (exception != null) {
                        throwableInfo = new ThrowableInformation(exception);
                    }
                    final LoggingEvent loggingEvent = new LoggingEvent(null, logger, timeStamp, level, message, threadName, throwableInfo, ndc, info, properties);
                    events.add(loggingEvent);
                    message = null;
                    ndc = null;
                    exception = null;
                    className = null;
                    methodName = null;
                    fileName = null;
                    lineNumber = null;
                    properties = null;
                }
            }
        }
        return events;
    }
    
    private String getCData(final Node n) {
        final StringBuffer buf = new StringBuffer();
        final NodeList nl = n.getChildNodes();
        for (int x = 0; x < nl.getLength(); ++x) {
            final Node innerNode = nl.item(x);
            if (innerNode.getNodeType() == 3 || innerNode.getNodeType() == 4) {
                buf.append(innerNode.getNodeValue());
            }
        }
        return buf.toString();
    }
}
