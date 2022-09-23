// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

public abstract class ConfigurationXMLReader implements XMLReader
{
    protected static final String NS_URI = "";
    private static final String DEFAULT_ROOT_NAME = "config";
    private static final Attributes EMPTY_ATTRS;
    private ContentHandler contentHandler;
    private SAXException exception;
    private String rootName;
    
    protected ConfigurationXMLReader() {
        this.rootName = "config";
    }
    
    @Override
    public void parse(final String systemId) throws IOException, SAXException {
        this.parseConfiguration();
    }
    
    @Override
    public void parse(final InputSource input) throws IOException, SAXException {
        this.parseConfiguration();
    }
    
    @Override
    public boolean getFeature(final String name) {
        return false;
    }
    
    @Override
    public void setFeature(final String name, final boolean value) {
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this.contentHandler = handler;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler handler) {
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }
    
    @Override
    public void setEntityResolver(final EntityResolver resolver) {
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
    }
    
    @Override
    public Object getProperty(final String name) {
        return null;
    }
    
    @Override
    public void setProperty(final String name, final Object value) {
    }
    
    public String getRootName() {
        return this.rootName;
    }
    
    public void setRootName(final String string) {
        this.rootName = string;
    }
    
    protected void fireElementStart(final String name, final Attributes attribs) {
        if (this.getException() == null) {
            try {
                final Attributes at = (attribs == null) ? ConfigurationXMLReader.EMPTY_ATTRS : attribs;
                this.getContentHandler().startElement("", name, name, at);
            }
            catch (SAXException ex) {
                this.exception = ex;
            }
        }
    }
    
    protected void fireElementEnd(final String name) {
        if (this.getException() == null) {
            try {
                this.getContentHandler().endElement("", name, name);
            }
            catch (SAXException ex) {
                this.exception = ex;
            }
        }
    }
    
    protected void fireCharacters(final String text) {
        if (this.getException() == null) {
            try {
                final char[] ch = text.toCharArray();
                this.getContentHandler().characters(ch, 0, ch.length);
            }
            catch (SAXException ex) {
                this.exception = ex;
            }
        }
    }
    
    public SAXException getException() {
        return this.exception;
    }
    
    protected void parseConfiguration() throws IOException, SAXException {
        if (this.getParsedConfiguration() == null) {
            throw new IOException("No configuration specified!");
        }
        if (this.getContentHandler() != null) {
            this.exception = null;
            this.getContentHandler().startDocument();
            this.processKeys();
            if (this.getException() != null) {
                throw this.getException();
            }
            this.getContentHandler().endDocument();
        }
    }
    
    public abstract Configuration getParsedConfiguration();
    
    protected abstract void processKeys() throws IOException, SAXException;
    
    static {
        EMPTY_ATTRS = new AttributesImpl();
    }
}
