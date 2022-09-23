// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.extras;

import javax.xml.transform.Transformer;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import javax.xml.transform.TransformerConfigurationException;
import org.w3c.dom.NodeList;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.util.Set;
import org.apache.log4j.spi.LocationInfo;
import javax.xml.transform.sax.TransformerHandler;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.log4j.helpers.MDCKeySetExtractor;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import javax.xml.transform.Result;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.spi.LoggingEvent;
import java.io.InputStream;
import org.apache.log4j.helpers.LogLog;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.text.DateFormat;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.xml.transform.TransformerFactory;
import org.apache.log4j.pattern.CachedDateFormat;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.SAXTransformerFactory;
import java.nio.charset.Charset;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.Layout;

public final class XSLTLayout extends Layout implements UnrecognizedElementHandler
{
    private static final String XSLT_NS = "http://www.w3.org/1999/XSL/Transform";
    private static final String LOG4J_NS = "http://jakarta.apache.org/log4j/";
    private boolean locationInfo;
    private String mediaType;
    private Charset encoding;
    private SAXTransformerFactory transformerFactory;
    private Templates templates;
    private final ByteArrayOutputStream outputStream;
    private boolean ignoresThrowable;
    private boolean properties;
    private boolean activated;
    private final CachedDateFormat utcDateFormat;
    
    public XSLTLayout() {
        this.locationInfo = false;
        this.mediaType = "text/plain";
        this.ignoresThrowable = false;
        this.properties = true;
        this.activated = false;
        this.outputStream = new ByteArrayOutputStream();
        this.transformerFactory = (SAXTransformerFactory)TransformerFactory.newInstance();
        final SimpleDateFormat zdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        zdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.utcDateFormat = new CachedDateFormat(zdf, 1000);
    }
    
    public synchronized String getContentType() {
        return this.mediaType;
    }
    
    public synchronized void setLocationInfo(final boolean flag) {
        this.locationInfo = flag;
    }
    
    public synchronized boolean getLocationInfo() {
        return this.locationInfo;
    }
    
    public synchronized void setProperties(final boolean flag) {
        this.properties = flag;
    }
    
    public synchronized boolean getProperties() {
        return this.properties;
    }
    
    public synchronized void activateOptions() {
        if (this.templates == null) {
            try {
                final InputStream is = XSLTLayout.class.getResourceAsStream("default.xslt");
                final StreamSource ss = new StreamSource(is);
                this.templates = this.transformerFactory.newTemplates(ss);
                this.encoding = Charset.forName("US-ASCII");
                this.mediaType = "text/plain";
            }
            catch (Exception ex) {
                LogLog.error("Error loading default.xslt", ex);
            }
        }
        this.activated = true;
    }
    
    public synchronized boolean ignoresThrowable() {
        return this.ignoresThrowable;
    }
    
    public synchronized void setIgnoresThrowable(final boolean ignoresThrowable) {
        this.ignoresThrowable = ignoresThrowable;
    }
    
    public synchronized String format(final LoggingEvent event) {
        if (!this.activated) {
            this.activateOptions();
        }
        if (this.templates != null && this.encoding != null) {
            this.outputStream.reset();
            try {
                final TransformerHandler transformer = this.transformerFactory.newTransformerHandler(this.templates);
                transformer.setResult(new StreamResult(this.outputStream));
                transformer.startDocument();
                final AttributesImpl attrs = new AttributesImpl();
                attrs.addAttribute(null, "logger", "logger", "CDATA", event.getLoggerName());
                attrs.addAttribute(null, "timestamp", "timestamp", "CDATA", Long.toString(event.timeStamp));
                attrs.addAttribute(null, "level", "level", "CDATA", event.getLevel().toString());
                attrs.addAttribute(null, "thread", "thread", "CDATA", event.getThreadName());
                final StringBuffer buf = new StringBuffer();
                this.utcDateFormat.format(event.timeStamp, buf);
                attrs.addAttribute(null, "time", "time", "CDATA", buf.toString());
                transformer.startElement("http://jakarta.apache.org/log4j/", "event", "event", attrs);
                attrs.clear();
                transformer.startElement("http://jakarta.apache.org/log4j/", "message", "message", attrs);
                final String msg = event.getRenderedMessage();
                if (msg != null && msg.length() > 0) {
                    transformer.characters(msg.toCharArray(), 0, msg.length());
                }
                transformer.endElement("http://jakarta.apache.org/log4j/", "message", "message");
                final String ndc = event.getNDC();
                if (ndc != null) {
                    transformer.startElement("http://jakarta.apache.org/log4j/", "NDC", "NDC", attrs);
                    final char[] ndcChars = ndc.toCharArray();
                    transformer.characters(ndcChars, 0, ndcChars.length);
                    transformer.endElement("http://jakarta.apache.org/log4j/", "NDC", "NDC");
                }
                if (!this.ignoresThrowable) {
                    final String[] s = event.getThrowableStrRep();
                    if (s != null) {
                        transformer.startElement("http://jakarta.apache.org/log4j/", "throwable", "throwable", attrs);
                        final char[] nl = { '\n' };
                        for (int i = 0; i < s.length; ++i) {
                            final char[] line = s[i].toCharArray();
                            transformer.characters(line, 0, line.length);
                            transformer.characters(nl, 0, nl.length);
                        }
                        transformer.endElement("http://jakarta.apache.org/log4j/", "throwable", "throwable");
                    }
                }
                if (this.locationInfo) {
                    final LocationInfo locationInfo = event.getLocationInformation();
                    attrs.addAttribute(null, "class", "class", "CDATA", locationInfo.getClassName());
                    attrs.addAttribute(null, "method", "method", "CDATA", locationInfo.getMethodName());
                    attrs.addAttribute(null, "file", "file", "CDATA", locationInfo.getFileName());
                    attrs.addAttribute(null, "line", "line", "CDATA", locationInfo.getLineNumber());
                    transformer.startElement("http://jakarta.apache.org/log4j/", "locationInfo", "locationInfo", attrs);
                    transformer.endElement("http://jakarta.apache.org/log4j/", "locationInfo", "locationInfo");
                }
                if (this.properties) {
                    final Set mdcKeySet = MDCKeySetExtractor.INSTANCE.getPropertyKeySet(event);
                    if (mdcKeySet != null && mdcKeySet.size() > 0) {
                        attrs.clear();
                        transformer.startElement("http://jakarta.apache.org/log4j/", "properties", "properties", attrs);
                        final Object[] keys = mdcKeySet.toArray();
                        Arrays.sort(keys);
                        for (int i = 0; i < keys.length; ++i) {
                            final String key = keys[i].toString();
                            final Object val = event.getMDC(key);
                            attrs.clear();
                            attrs.addAttribute(null, "name", "name", "CDATA", key);
                            attrs.addAttribute(null, "value", "value", "CDATA", val.toString());
                            transformer.startElement("http://jakarta.apache.org/log4j/", "data", "data", attrs);
                            transformer.endElement("http://jakarta.apache.org/log4j/", "data", "data");
                        }
                    }
                }
                transformer.endElement("http://jakarta.apache.org/log4j/", "event", "event");
                transformer.endDocument();
                final String body = this.encoding.decode(ByteBuffer.wrap(this.outputStream.toByteArray())).toString();
                this.outputStream.reset();
                if (body.startsWith("<?xml ")) {
                    int endDecl = body.indexOf("?>");
                    if (endDecl != -1) {
                        for (endDecl += 2; endDecl < body.length() && (body.charAt(endDecl) == '\n' || body.charAt(endDecl) == '\r'); ++endDecl) {}
                        return body.substring(endDecl);
                    }
                }
                return body;
            }
            catch (Exception ex) {
                LogLog.error("Error during transformation", ex);
                return ex.toString();
            }
        }
        return "No valid transform or encoding specified.";
    }
    
    public void setTransform(final Document xsltdoc) throws TransformerConfigurationException {
        String encodingName = null;
        this.mediaType = null;
        String method = null;
        final NodeList nodes = xsltdoc.getElementsByTagNameNS("http://www.w3.org/1999/XSL/Transform", "output");
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Element outputElement = (Element)nodes.item(i);
            if (method == null || method.length() == 0) {
                method = outputElement.getAttributeNS(null, "method");
            }
            if (encodingName == null || encodingName.length() == 0) {
                encodingName = outputElement.getAttributeNS(null, "encoding");
            }
            if (this.mediaType == null || this.mediaType.length() == 0) {
                this.mediaType = outputElement.getAttributeNS(null, "media-type");
            }
        }
        if (this.mediaType == null || this.mediaType.length() == 0) {
            if ("html".equals(method)) {
                this.mediaType = "text/html";
            }
            else if ("xml".equals(method)) {
                this.mediaType = "text/xml";
            }
            else {
                this.mediaType = "text/plain";
            }
        }
        if (encodingName == null || encodingName.length() == 0) {
            final Element transformElement = xsltdoc.getDocumentElement();
            final Element outputElement = xsltdoc.createElementNS("http://www.w3.org/1999/XSL/Transform", "output");
            outputElement.setAttributeNS(null, "encoding", "US-ASCII");
            transformElement.insertBefore(outputElement, transformElement.getFirstChild());
            this.encoding = Charset.forName("US-ASCII");
        }
        else {
            this.encoding = Charset.forName(encodingName);
        }
        final DOMSource transformSource = new DOMSource(xsltdoc);
        this.templates = this.transformerFactory.newTemplates(transformSource);
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        if ("http://www.w3.org/1999/XSL/Transform".equals(element.getNamespaceURI()) || element.getNodeName().indexOf("transform") != -1 || element.getNodeName().indexOf("stylesheet") != -1) {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            final DOMSource source = new DOMSource(element);
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, new StreamResult(os));
            final ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            final Document xsltdoc = domFactory.newDocumentBuilder().parse(is);
            this.setTransform(xsltdoc);
            return true;
        }
        return false;
    }
}
