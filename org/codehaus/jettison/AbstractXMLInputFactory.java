// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.stream.StreamSource;
import java.io.CharArrayWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.codehaus.jettison.json.JSONTokener;
import javax.xml.transform.Source;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

public abstract class AbstractXMLInputFactory extends XMLInputFactory
{
    static final int INPUT_BUF_SIZE = 512;
    
    public XMLEventReader createFilteredReader(final XMLEventReader arg0, final EventFilter arg1) throws XMLStreamException {
        return null;
    }
    
    public XMLStreamReader createFilteredReader(final XMLStreamReader arg0, final StreamFilter arg1) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final InputStream arg0, final String encoding) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final InputStream arg0) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final Reader arg0) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final Source arg0) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final String systemId, final InputStream arg1) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final String systemId, final Reader arg1) throws XMLStreamException {
        return null;
    }
    
    public XMLEventReader createXMLEventReader(final XMLStreamReader arg0) throws XMLStreamException {
        return null;
    }
    
    public XMLStreamReader createXMLStreamReader(final InputStream is) throws XMLStreamException {
        return this.createXMLStreamReader(is, null);
    }
    
    public XMLStreamReader createXMLStreamReader(final InputStream is, String charset) throws XMLStreamException {
        if (charset == null) {
            charset = "UTF-8";
        }
        try {
            final String doc = this.readAll(is, charset);
            return this.createXMLStreamReader(new JSONTokener(doc));
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private String readAll(final InputStream in, final String encoding) throws IOException {
        final byte[] buffer = new byte[512];
        ByteArrayOutputStream bos = null;
        while (true) {
            final int count = in.read(buffer);
            if (count < 0) {
                break;
            }
            if (bos == null) {
                int cap;
                if (count < 64) {
                    cap = 64;
                }
                else if (count == 512) {
                    cap = 2048;
                }
                else {
                    cap = count;
                }
                bos = new ByteArrayOutputStream(cap);
            }
            bos.write(buffer, 0, count);
        }
        return (bos == null) ? "" : bos.toString(encoding);
    }
    
    public abstract XMLStreamReader createXMLStreamReader(final JSONTokener p0) throws XMLStreamException;
    
    public XMLStreamReader createXMLStreamReader(final Reader reader) throws XMLStreamException {
        try {
            return this.createXMLStreamReader(new JSONTokener(this.readAll(reader)));
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private String readAll(final Reader r) throws IOException {
        final char[] buf = new char[512];
        int len = 0;
        do {
            final int count = r.read(buf, len, buf.length - len);
            if (count < 0) {
                return (len == 0) ? "" : new String(buf, 0, len);
            }
            len += count;
        } while (len < buf.length);
        final CharArrayWriter wrt = new CharArrayWriter(2048);
        wrt.write(buf, 0, len);
        while ((len = r.read(buf)) != -1) {
            wrt.write(buf, 0, len);
        }
        return wrt.toString();
    }
    
    public XMLStreamReader createXMLStreamReader(final Source src) throws XMLStreamException {
        if (!(src instanceof StreamSource)) {
            throw new UnsupportedOperationException("Only javax.xml.transform.stream.StreamSource type supported");
        }
        final StreamSource ss = (StreamSource)src;
        final InputStream in = ss.getInputStream();
        final String systemId = ss.getSystemId();
        if (in != null) {
            if (systemId != null) {
                return this.createXMLStreamReader(systemId, in);
            }
            return this.createXMLStreamReader(in);
        }
        else {
            final Reader r = ss.getReader();
            if (r == null) {
                throw new UnsupportedOperationException("Only those javax.xml.transform.stream.StreamSource instances supported that have an InputStream or Reader");
            }
            if (systemId != null) {
                return this.createXMLStreamReader(systemId, r);
            }
            return this.createXMLStreamReader(r);
        }
    }
    
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream arg1) throws XMLStreamException {
        return this.createXMLStreamReader(arg1, null);
    }
    
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader r) throws XMLStreamException {
        return this.createXMLStreamReader(r);
    }
    
    public XMLEventAllocator getEventAllocator() {
        return null;
    }
    
    public Object getProperty(final String arg0) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }
    
    public XMLReporter getXMLReporter() {
        return null;
    }
    
    public XMLResolver getXMLResolver() {
        return null;
    }
    
    public boolean isPropertySupported(final String arg0) {
        return false;
    }
    
    public void setEventAllocator(final XMLEventAllocator arg0) {
    }
    
    public void setProperty(final String arg0, final Object arg1) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }
    
    public void setXMLReporter(final XMLReporter arg0) {
    }
    
    public void setXMLResolver(final XMLResolver arg0) {
    }
}
