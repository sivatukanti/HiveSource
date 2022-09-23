// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dom;

import com.ctc.wstx.exc.WstxParsingException;
import javax.xml.stream.Location;
import java.util.Collections;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import com.ctc.wstx.api.ReaderConfig;
import org.codehaus.stax2.ri.dom.DOMWrappingReader;

public class WstxDOMWrappingReader extends DOMWrappingReader
{
    protected final ReaderConfig mConfig;
    
    protected WstxDOMWrappingReader(final DOMSource src, final ReaderConfig cfg) throws XMLStreamException {
        super(src, cfg.willSupportNamespaces(), cfg.willCoalesceText());
        this.mConfig = cfg;
        if (cfg.hasInternNamesBeenEnabled()) {
            this.setInternNames(true);
        }
        if (cfg.hasInternNsURIsBeenEnabled()) {
            this.setInternNsURIs(true);
        }
    }
    
    public static WstxDOMWrappingReader createFrom(final DOMSource src, final ReaderConfig cfg) throws XMLStreamException {
        return new WstxDOMWrappingReader(src, cfg);
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.mConfig.isPropertySupported(name);
    }
    
    @Override
    public Object getProperty(final String name) {
        if (name.equals("javax.xml.stream.entities")) {
            return Collections.EMPTY_LIST;
        }
        if (name.equals("javax.xml.stream.notations")) {
            return Collections.EMPTY_LIST;
        }
        return this.mConfig.getProperty(name);
    }
    
    @Override
    public boolean setProperty(final String name, final Object value) {
        return this.mConfig.setProperty(name, value);
    }
    
    @Override
    protected void throwStreamException(final String msg, final Location loc) throws XMLStreamException {
        if (loc == null) {
            throw new WstxParsingException(msg);
        }
        throw new WstxParsingException(msg, loc);
    }
}
