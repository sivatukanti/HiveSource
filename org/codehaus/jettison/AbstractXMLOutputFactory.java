// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.transform.stream.StreamResult;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;

public abstract class AbstractXMLOutputFactory extends XMLOutputFactory
{
    public XMLEventWriter createXMLEventWriter(final OutputStream out, final String charset) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(out, charset));
    }
    
    public XMLEventWriter createXMLEventWriter(final OutputStream out) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(out));
    }
    
    public XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(result));
    }
    
    public XMLEventWriter createXMLEventWriter(final Writer writer) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(writer));
    }
    
    public XMLStreamWriter createXMLStreamWriter(final OutputStream out, String charset) throws XMLStreamException {
        if (charset == null) {
            charset = "UTF-8";
        }
        try {
            return this.createXMLStreamWriter(new OutputStreamWriter(out, charset));
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public XMLStreamWriter createXMLStreamWriter(final OutputStream out) throws XMLStreamException {
        return this.createXMLStreamWriter(out, null);
    }
    
    public XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        if (!(result instanceof StreamResult)) {
            throw new UnsupportedOperationException("Only javax.xml.transform.stream.StreamResult type supported");
        }
        final StreamResult sr = (StreamResult)result;
        final OutputStream out = sr.getOutputStream();
        if (out != null) {
            return this.createXMLStreamWriter(out);
        }
        final Writer w = sr.getWriter();
        if (w != null) {
            return this.createXMLStreamWriter(w);
        }
        throw new UnsupportedOperationException("Only those javax.xml.transform.stream.StreamResult instances supported that have an OutputStream or Writer");
    }
    
    public abstract XMLStreamWriter createXMLStreamWriter(final Writer p0) throws XMLStreamException;
    
    public Object getProperty(final String arg0) throws IllegalArgumentException {
        return null;
    }
    
    public boolean isPropertySupported(final String arg0) {
        return false;
    }
    
    public void setProperty(final String arg0, final Object arg1) throws IllegalArgumentException {
    }
}
