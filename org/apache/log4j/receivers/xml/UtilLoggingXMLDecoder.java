// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.xml;

import java.util.Iterator;
import org.apache.log4j.Level;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.log4j.Category;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import java.util.ArrayList;
import org.apache.log4j.helpers.UtilLoggingLevel;
import org.apache.log4j.Logger;
import java.util.Hashtable;
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
import org.xml.sax.ErrorHandler;
import org.apache.log4j.xml.SAXErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.awt.Component;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import org.apache.log4j.receivers.spi.Decoder;

public class UtilLoggingXMLDecoder implements Decoder
{
    private static final String BEGIN_PART = "<log>";
    private static final String END_PART = "</log>";
    private DocumentBuilder docBuilder;
    private Map additionalProperties;
    private String partialEvent;
    private static final String RECORD_END = "</record>";
    private Component owner;
    private static final String ENCODING = "UTF-8";
    
    public UtilLoggingXMLDecoder(final Component o) {
        this();
        this.owner = o;
    }
    
    public UtilLoggingXMLDecoder() {
        this.additionalProperties = new HashMap();
        this.owner = null;
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try {
            (this.docBuilder = dbf.newDocumentBuilder()).setErrorHandler(new SAXErrorHandler());
            this.docBuilder.setEntityResolver(new UtilLoggingEntityResolver());
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
            if (!data.startsWith("<?xml")) {
                buf.append("<log>");
            }
            buf.append(data);
            if (!data.endsWith("</log>")) {
                buf.append("</log>");
            }
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
        String newPartialEvent = null;
        if (document.lastIndexOf("</record>") == -1) {
            this.partialEvent += document;
            return null;
        }
        String newDoc;
        if (document.lastIndexOf("</record>") + "</record>".length() < document.length()) {
            newDoc = document.substring(0, document.lastIndexOf("</record>") + "</record>".length());
            newPartialEvent = document.substring(document.lastIndexOf("</record>") + "</record>".length());
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
        final NodeList eventList = document.getElementsByTagName("record");
        for (int eventIndex = 0; eventIndex < eventList.getLength(); ++eventIndex) {
            final Node eventNode = eventList.item(eventIndex);
            Logger logger = null;
            long timeStamp = 0L;
            Level level = null;
            String threadName = null;
            Object message = null;
            final String ndc = null;
            String[] exception = null;
            String className = null;
            String methodName = null;
            final String fileName = null;
            final String lineNumber = null;
            Hashtable properties = new Hashtable();
            final NodeList list = eventNode.getChildNodes();
            final int listLength = list.getLength();
            if (listLength != 0) {
                for (int y = 0; y < listLength; ++y) {
                    final String tagName = list.item(y).getNodeName();
                    if (tagName.equalsIgnoreCase("logger")) {
                        logger = Logger.getLogger(this.getCData(list.item(y)));
                    }
                    if (tagName.equalsIgnoreCase("millis")) {
                        timeStamp = Long.parseLong(this.getCData(list.item(y)));
                    }
                    if (tagName.equalsIgnoreCase("level")) {
                        level = UtilLoggingLevel.toLevel(this.getCData(list.item(y)));
                    }
                    if (tagName.equalsIgnoreCase("thread")) {
                        threadName = this.getCData(list.item(y));
                    }
                    if (tagName.equalsIgnoreCase("sequence")) {
                        properties.put("log4jid", this.getCData(list.item(y)));
                    }
                    if (tagName.equalsIgnoreCase("message")) {
                        message = this.getCData(list.item(y));
                    }
                    if (tagName.equalsIgnoreCase("class")) {
                        className = this.getCData(list.item(y));
                    }
                    if (tagName.equalsIgnoreCase("method")) {
                        methodName = this.getCData(list.item(y));
                    }
                    if (tagName.equalsIgnoreCase("exception")) {
                        final ArrayList exceptionList = new ArrayList();
                        final NodeList exList = list.item(y).getChildNodes();
                        for (int exlistLength = exList.getLength(), i2 = 0; i2 < exlistLength; ++i2) {
                            final Node exNode = exList.item(i2);
                            final String exName = exList.item(i2).getNodeName();
                            if (exName.equalsIgnoreCase("message")) {
                                exceptionList.add(this.getCData(exList.item(i2)));
                            }
                            if (exName.equalsIgnoreCase("frame")) {
                                final NodeList exList2 = exNode.getChildNodes();
                                for (int exlist2Length = exList2.getLength(), i3 = 0; i3 < exlist2Length; ++i3) {
                                    exceptionList.add(this.getCData(exList2.item(i3)) + "\n");
                                }
                            }
                        }
                        if (exceptionList.size() > 0) {
                            exception = exceptionList.toArray(new String[exceptionList.size()]);
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
