// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.stax;

import com.ctc.wstx.util.URLUtil;
import com.ctc.wstx.dom.WstxDOMWrappingWriter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import com.ctc.wstx.exc.WstxIOException;
import org.codehaus.stax2.io.Stax2Result;
import com.ctc.wstx.sw.SimpleNsStreamWriter;
import com.ctc.wstx.sw.RepairingNsStreamWriter;
import com.ctc.wstx.sw.NonNsStreamWriter;
import com.ctc.wstx.sw.XmlWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import com.ctc.wstx.sw.AsciiXmlWriter;
import com.ctc.wstx.sw.ISOLatin1XmlWriter;
import com.ctc.wstx.sw.BufferingXmlWriter;
import com.ctc.wstx.io.UTF8Writer;
import com.ctc.wstx.io.CharsetNames;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.Stax2WriterAdapter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import org.codehaus.stax2.ri.Stax2EventWriterImpl;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.OutputStream;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.OutputConfigFlags;
import org.codehaus.stax2.XMLOutputFactory2;

public class WstxOutputFactory extends XMLOutputFactory2 implements OutputConfigFlags
{
    protected final WriterConfig mConfig;
    
    public WstxOutputFactory() {
        this.mConfig = WriterConfig.createFullDefaults();
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream out) throws XMLStreamException {
        return this.createXMLEventWriter(out, null);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream out, final String enc) throws XMLStreamException {
        if (out == null) {
            throw new IllegalArgumentException("Null OutputStream is not a valid argument");
        }
        return new Stax2EventWriterImpl(this.createSW(out, null, enc, false));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        return new Stax2EventWriterImpl(this.createSW(result));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Writer w) throws XMLStreamException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer is not a valid argument");
        }
        return new Stax2EventWriterImpl(this.createSW(null, w, null, false));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream out) throws XMLStreamException {
        return this.createXMLStreamWriter(out, null);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream out, final String enc) throws XMLStreamException {
        if (out == null) {
            throw new IllegalArgumentException("Null OutputStream is not a valid argument");
        }
        return this.createSW(out, null, enc, false);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        return this.createSW(result);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Writer w) throws XMLStreamException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer is not a valid argument");
        }
        return this.createSW(null, w, null, false);
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.mConfig.getProperty(name);
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.mConfig.isPropertySupported(name);
    }
    
    @Override
    public void setProperty(final String name, final Object value) {
        this.mConfig.setProperty(name, value);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Writer w, final String enc) throws XMLStreamException {
        return new Stax2EventWriterImpl(this.createSW(null, w, enc, false));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final XMLStreamWriter sw) throws XMLStreamException {
        final XMLStreamWriter2 sw2 = Stax2WriterAdapter.wrapIfNecessary(sw);
        return new Stax2EventWriterImpl(sw2);
    }
    
    @Override
    public XMLStreamWriter2 createXMLStreamWriter(final Writer w, final String enc) throws XMLStreamException {
        return this.createSW(null, w, enc, false);
    }
    
    @Override
    public void configureForXmlConformance() {
        this.mConfig.configureForXmlConformance();
    }
    
    @Override
    public void configureForRobustness() {
        this.mConfig.configureForRobustness();
    }
    
    @Override
    public void configureForSpeed() {
        this.mConfig.configureForSpeed();
    }
    
    public WriterConfig getConfig() {
        return this.mConfig;
    }
    
    private XMLStreamWriter2 createSW(final OutputStream out, Writer w, String enc, final boolean requireAutoClose) throws XMLStreamException {
        final WriterConfig cfg = this.mConfig.createNonShared();
        final boolean autoCloseOutput = requireAutoClose || this.mConfig.willAutoCloseOutput();
        if (w == null) {
            if (enc == null) {
                enc = "UTF-8";
            }
            else if (enc != "UTF-8" && enc != "ISO-8859-1" && enc != "US-ASCII") {
                enc = CharsetNames.normalize(enc);
            }
            try {
                XmlWriter xw;
                if (enc == "UTF-8") {
                    w = new UTF8Writer(cfg, out, autoCloseOutput);
                    xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, out, 16);
                }
                else if (enc == "ISO-8859-1") {
                    xw = new ISOLatin1XmlWriter(out, cfg, autoCloseOutput);
                }
                else if (enc == "US-ASCII") {
                    xw = new AsciiXmlWriter(out, cfg, autoCloseOutput);
                }
                else {
                    w = new OutputStreamWriter(out, enc);
                    xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, out, -1);
                }
                return this.createSW(enc, cfg, xw);
            }
            catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        }
        if (enc == null) {
            enc = CharsetNames.findEncodingFor(w);
        }
        XmlWriter xw;
        try {
            xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, null, -1);
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
        return this.createSW(enc, cfg, xw);
    }
    
    protected XMLStreamWriter2 createSW(final String enc, final WriterConfig cfg, final XmlWriter xw) {
        if (!cfg.willSupportNamespaces()) {
            return new NonNsStreamWriter(xw, enc, cfg);
        }
        if (cfg.automaticNamespacesEnabled()) {
            return new RepairingNsStreamWriter(xw, enc, cfg);
        }
        return new SimpleNsStreamWriter(xw, enc, cfg);
    }
    
    private XMLStreamWriter2 createSW(final Result res) throws XMLStreamException {
        OutputStream out = null;
        Writer w = null;
        final String encoding = null;
        String sysId = null;
        boolean requireAutoClose;
        if (res instanceof Stax2Result) {
            final Stax2Result sr = (Stax2Result)res;
            try {
                out = sr.constructOutputStream();
                if (out == null) {
                    w = sr.constructWriter();
                }
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            requireAutoClose = true;
        }
        else if (res instanceof StreamResult) {
            final StreamResult sr2 = (StreamResult)res;
            out = sr2.getOutputStream();
            sysId = sr2.getSystemId();
            if (out == null) {
                w = sr2.getWriter();
            }
            requireAutoClose = false;
        }
        else if (res instanceof SAXResult) {
            final SAXResult sr3 = (SAXResult)res;
            sysId = sr3.getSystemId();
            if (sysId == null || sysId.length() == 0) {
                throw new XMLStreamException("Can not create a stream writer for a SAXResult that does not have System Id (support for using SAX input source not implemented)");
            }
            requireAutoClose = true;
        }
        else {
            if (res instanceof DOMResult) {
                return WstxDOMWrappingWriter.createFrom(this.mConfig.createNonShared(), (DOMResult)res);
            }
            throw new IllegalArgumentException("Can not instantiate a writer for XML result type " + res.getClass() + " (unrecognized type)");
        }
        if (out != null) {
            return this.createSW(out, null, encoding, requireAutoClose);
        }
        if (w != null) {
            return this.createSW(null, w, encoding, requireAutoClose);
        }
        if (sysId != null && sysId.length() > 0) {
            requireAutoClose = true;
            try {
                out = URLUtil.outputStreamFromURL(URLUtil.urlFromSystemId(sysId));
            }
            catch (IOException ioe2) {
                throw new WstxIOException(ioe2);
            }
            return this.createSW(out, null, encoding, requireAutoClose);
        }
        throw new XMLStreamException("Can not create Stax writer for passed-in Result -- neither writer, output stream or system id was accessible");
    }
}
