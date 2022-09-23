// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.w3c.dom.Document;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import java.io.PrintWriter;
import java.io.Writer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.SAXParserFactory;
import java.io.Reader;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.w3c.dom.Element;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;

public class XMLPropertiesConfiguration extends BaseConfiguration implements FileBasedConfiguration, FileLocatorAware
{
    public static final String DEFAULT_ENCODING = "UTF-8";
    private static final String MALFORMED_XML_EXCEPTION = "Malformed XML";
    private FileLocator locator;
    private String header;
    
    public XMLPropertiesConfiguration() {
    }
    
    public XMLPropertiesConfiguration(final Element element) throws ConfigurationException {
        this.load(element);
    }
    
    public String getHeader() {
        return this.header;
    }
    
    public void setHeader(final String header) {
        this.header = header;
    }
    
    @Override
    public void read(final Reader in) throws ConfigurationException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(true);
        try {
            final SAXParser parser = factory.newSAXParser();
            final XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(final String publicId, final String systemId) {
                    return new InputSource(this.getClass().getClassLoader().getResourceAsStream("properties.dtd"));
                }
            });
            xmlReader.setContentHandler(new XMLPropertiesHandler());
            xmlReader.parse(new InputSource(in));
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to parse the configuration file", e);
        }
    }
    
    public void load(final Element element) throws ConfigurationException {
        if (!element.getNodeName().equals("properties")) {
            throw new ConfigurationException("Malformed XML");
        }
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            if (item instanceof Element) {
                if (item.getNodeName().equals("comment")) {
                    this.setHeader(item.getTextContent());
                }
                else {
                    if (!item.getNodeName().equals("entry")) {
                        throw new ConfigurationException("Malformed XML");
                    }
                    final String key = ((Element)item).getAttribute("key");
                    this.addProperty(key, item.getTextContent());
                }
            }
        }
    }
    
    @Override
    public void write(final Writer out) throws ConfigurationException {
        final PrintWriter writer = new PrintWriter(out);
        String encoding = (this.locator != null) ? this.locator.getEncoding() : null;
        if (encoding == null) {
            encoding = "UTF-8";
        }
        writer.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        writer.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
        writer.println("<properties>");
        if (this.getHeader() != null) {
            writer.println("  <comment>" + StringEscapeUtils.escapeXml(this.getHeader()) + "</comment>");
        }
        final Iterator<String> keys = this.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = this.getProperty(key);
            if (value instanceof List) {
                this.writeProperty(writer, key, (List<?>)value);
            }
            else {
                this.writeProperty(writer, key, value);
            }
        }
        writer.println("</properties>");
        writer.flush();
    }
    
    private void writeProperty(final PrintWriter out, final String key, final Object value) {
        final String k = StringEscapeUtils.escapeXml(key);
        if (value != null) {
            final String v = this.escapeValue(value);
            out.println("  <entry key=\"" + k + "\">" + v + "</entry>");
        }
        else {
            out.println("  <entry key=\"" + k + "\"/>");
        }
    }
    
    private void writeProperty(final PrintWriter out, final String key, final List<?> values) {
        for (final Object value : values) {
            this.writeProperty(out, key, value);
        }
    }
    
    public void save(final Document document, final Node parent) {
        final Element properties = document.createElement("properties");
        parent.appendChild(properties);
        if (this.getHeader() != null) {
            final Element comment = document.createElement("comment");
            properties.appendChild(comment);
            comment.setTextContent(StringEscapeUtils.escapeXml(this.getHeader()));
        }
        final Iterator<String> keys = this.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = this.getProperty(key);
            if (value instanceof List) {
                this.writeProperty(document, properties, key, (List<?>)value);
            }
            else {
                this.writeProperty(document, properties, key, value);
            }
        }
    }
    
    @Override
    public void initFileLocator(final FileLocator locator) {
        this.locator = locator;
    }
    
    private void writeProperty(final Document document, final Node properties, final String key, final Object value) {
        final Element entry = document.createElement("entry");
        properties.appendChild(entry);
        final String k = StringEscapeUtils.escapeXml(key);
        entry.setAttribute("key", k);
        if (value != null) {
            final String v = this.escapeValue(value);
            entry.setTextContent(v);
        }
    }
    
    private void writeProperty(final Document document, final Node properties, final String key, final List<?> values) {
        for (final Object value : values) {
            this.writeProperty(document, properties, key, value);
        }
    }
    
    private String escapeValue(final Object value) {
        final String v = StringEscapeUtils.escapeXml(String.valueOf(value));
        return String.valueOf(this.getListDelimiterHandler().escape(v, ListDelimiterHandler.NOOP_TRANSFORMER));
    }
    
    private class XMLPropertiesHandler extends DefaultHandler
    {
        private String key;
        private StringBuilder value;
        private boolean inCommentElement;
        private boolean inEntryElement;
        
        private XMLPropertiesHandler() {
            this.value = new StringBuilder();
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) {
            if ("comment".equals(qName)) {
                this.inCommentElement = true;
            }
            if ("entry".equals(qName)) {
                this.key = attrs.getValue("key");
                this.inEntryElement = true;
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            if (this.inCommentElement) {
                XMLPropertiesConfiguration.this.setHeader(this.value.toString());
                this.inCommentElement = false;
            }
            if (this.inEntryElement) {
                XMLPropertiesConfiguration.this.addProperty(this.key, this.value.toString());
                this.inEntryElement = false;
            }
            this.value = new StringBuilder();
        }
        
        @Override
        public void characters(final char[] chars, final int start, final int length) {
            this.value.append(chars, start, length);
        }
    }
}
