// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.stax;

import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.evt.DefaultEventAllocator;
import org.xml.sax.InputSource;
import com.ctc.wstx.dom.WstxDOMWrappingReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.codehaus.stax2.io.Stax2ByteArraySource;
import org.codehaus.stax2.io.Stax2Source;
import java.io.FileInputStream;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.util.URLUtil;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.sr.ValidatingStreamReader;
import com.ctc.wstx.io.InputSourceFactory;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.InputBootstrapper;
import org.codehaus.stax2.XMLStreamReader2;
import java.io.File;
import org.codehaus.stax2.XMLEventReader2;
import java.net.URL;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import javax.xml.transform.Source;
import java.io.Reader;
import com.ctc.wstx.evt.WstxEventReader;
import com.ctc.wstx.io.SystemId;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.Stax2FilteredStreamReader;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.ri.evt.Stax2FilteredEventReader;
import org.codehaus.stax2.ri.evt.Stax2EventReaderAdapter;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDId;
import com.ctc.wstx.util.SimpleCache;
import javax.xml.stream.util.XMLEventAllocator;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.sr.ReaderCreator;
import org.codehaus.stax2.XMLInputFactory2;

public class WstxInputFactory extends XMLInputFactory2 implements ReaderCreator, InputConfigFlags
{
    static final int MAX_SYMBOL_TABLE_SIZE = 12000;
    static final int MAX_SYMBOL_TABLE_GENERATIONS = 500;
    protected final ReaderConfig mConfig;
    protected XMLEventAllocator mAllocator;
    protected SimpleCache<DTDId, DTDSubset> mDTDCache;
    static final SymbolTable mRootSymbols;
    private SymbolTable mSymbols;
    
    public WstxInputFactory() {
        this.mAllocator = null;
        this.mDTDCache = null;
        this.mSymbols = WstxInputFactory.mRootSymbols;
        this.mConfig = ReaderConfig.createFullDefaults();
    }
    
    public void addSymbol(final String symbol) {
        synchronized (this.mSymbols) {
            this.mSymbols.findSymbol(symbol);
        }
    }
    
    @Override
    public synchronized DTDSubset findCachedDTD(final DTDId id) {
        return (this.mDTDCache == null) ? null : this.mDTDCache.find(id);
    }
    
    @Override
    public synchronized void updateSymbolTable(final SymbolTable t) {
        final SymbolTable curr = this.mSymbols;
        if (t.isDirectChildOf(curr)) {
            if (t.size() > 12000 || t.version() > 500) {
                this.mSymbols = WstxInputFactory.mRootSymbols;
            }
            else {
                this.mSymbols.mergeChild(t);
            }
        }
    }
    
    @Override
    public synchronized void addCachedDTD(final DTDId id, final DTDSubset extSubset) {
        if (this.mDTDCache == null) {
            this.mDTDCache = new SimpleCache<DTDId, DTDSubset>(this.mConfig.getDtdCacheSize());
        }
        this.mDTDCache.add(id, extSubset);
    }
    
    @Override
    public XMLEventReader createFilteredReader(final XMLEventReader reader, final EventFilter filter) {
        return new Stax2FilteredEventReader(Stax2EventReaderAdapter.wrapIfNecessary(reader), filter);
    }
    
    @Override
    public XMLStreamReader createFilteredReader(final XMLStreamReader reader, final StreamFilter filter) throws XMLStreamException {
        final Stax2FilteredStreamReader fr = new Stax2FilteredStreamReader(reader, filter);
        if (!filter.accept(fr)) {
            fr.next();
        }
        return fr;
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream in) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(null, in, null, true, false));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream in, final String enc) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(null, in, enc, true, false));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Reader r) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(null, r, true, false));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Source source) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(source, true));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final InputStream in) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(SystemId.construct(systemId), in, null, true, false));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final Reader r) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(SystemId.construct(systemId), r, true, false));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final XMLStreamReader sr) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), Stax2ReaderAdapter.wrapIfNecessary(sr));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream in) throws XMLStreamException {
        return this.createSR(null, in, null, false, false);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream in, final String enc) throws XMLStreamException {
        return this.createSR(null, in, enc, false, false);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader r) throws XMLStreamException {
        return this.createSR(null, r, false, false);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Source src) throws XMLStreamException {
        return this.createSR(src, false);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream in) throws XMLStreamException {
        return this.createSR(SystemId.construct(systemId), in, null, false, false);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader r) throws XMLStreamException {
        return this.createSR(SystemId.construct(systemId), r, false, false);
    }
    
    @Override
    public Object getProperty(final String name) {
        final Object ob = this.mConfig.getProperty(name);
        if (ob == null && name.equals("javax.xml.stream.allocator")) {
            return this.getEventAllocator();
        }
        return ob;
    }
    
    @Override
    public void setProperty(final String propName, final Object value) {
        if (!this.mConfig.setProperty(propName, value) && "javax.xml.stream.allocator".equals(propName)) {
            this.setEventAllocator((XMLEventAllocator)value);
        }
    }
    
    @Override
    public XMLEventAllocator getEventAllocator() {
        return this.mAllocator;
    }
    
    @Override
    public XMLReporter getXMLReporter() {
        return this.mConfig.getXMLReporter();
    }
    
    @Override
    public XMLResolver getXMLResolver() {
        return this.mConfig.getXMLResolver();
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.mConfig.isPropertySupported(name);
    }
    
    @Override
    public void setEventAllocator(final XMLEventAllocator allocator) {
        this.mAllocator = allocator;
    }
    
    @Override
    public void setXMLReporter(final XMLReporter r) {
        this.mConfig.setXMLReporter(r);
    }
    
    @Override
    public void setXMLResolver(final XMLResolver r) {
        this.mConfig.setXMLResolver(r);
    }
    
    @Override
    public XMLEventReader2 createXMLEventReader(final URL src) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(this.createPrivateConfig(), src, true, true));
    }
    
    @Override
    public XMLEventReader2 createXMLEventReader(final File f) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(f, true, true));
    }
    
    @Override
    public XMLStreamReader2 createXMLStreamReader(final URL src) throws XMLStreamException {
        return this.createSR(this.createPrivateConfig(), src, false, true);
    }
    
    @Override
    public XMLStreamReader2 createXMLStreamReader(final File f) throws XMLStreamException {
        return this.createSR(f, false, true);
    }
    
    @Override
    public void configureForXmlConformance() {
        this.mConfig.configureForXmlConformance();
    }
    
    @Override
    public void configureForConvenience() {
        this.mConfig.configureForConvenience();
    }
    
    @Override
    public void configureForSpeed() {
        this.mConfig.configureForSpeed();
    }
    
    @Override
    public void configureForLowMemUsage() {
        this.mConfig.configureForLowMemUsage();
    }
    
    @Override
    public void configureForRoundTripping() {
        this.mConfig.configureForRoundTripping();
    }
    
    public ReaderConfig getConfig() {
        return this.mConfig;
    }
    
    private XMLStreamReader2 doCreateSR(final ReaderConfig cfg, final SystemId systemId, final InputBootstrapper bs, final boolean forER, boolean autoCloseInput) throws XMLStreamException {
        if (!autoCloseInput) {
            autoCloseInput = cfg.willAutoCloseInput();
        }
        Reader r;
        try {
            r = bs.bootstrapInput(cfg, true, 0);
            if (bs.declaredXml11()) {
                cfg.enableXml11(true);
            }
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
        final BranchingReaderSource input = InputSourceFactory.constructDocumentSource(cfg, bs, null, systemId, r, autoCloseInput);
        return ValidatingStreamReader.createValidatingStreamReader(input, this, cfg, bs, forER);
    }
    
    public XMLStreamReader2 createSR(final ReaderConfig cfg, final String systemId, final InputBootstrapper bs, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        URL src = cfg.getBaseURL();
        if (src == null && systemId != null && systemId.length() > 0) {
            try {
                src = URLUtil.urlFromSystemId(systemId);
            }
            catch (IOException ie) {
                throw new WstxIOException(ie);
            }
        }
        return this.doCreateSR(cfg, SystemId.construct(systemId, src), bs, forER, autoCloseInput);
    }
    
    public XMLStreamReader2 createSR(final ReaderConfig cfg, final SystemId systemId, final InputBootstrapper bs, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        return this.doCreateSR(cfg, systemId, bs, forER, autoCloseInput);
    }
    
    protected XMLStreamReader2 createSR(final SystemId systemId, final InputStream in, final String enc, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        if (in == null) {
            throw new IllegalArgumentException("Null InputStream is not a valid argument");
        }
        final ReaderConfig cfg = this.createPrivateConfig();
        if (enc == null || enc.length() == 0) {
            return this.createSR(cfg, systemId, StreamBootstrapper.getInstance(null, systemId, in), forER, autoCloseInput);
        }
        final Reader r = DefaultInputResolver.constructOptimizedReader(cfg, in, false, enc);
        return this.createSR(cfg, systemId, ReaderBootstrapper.getInstance(null, systemId, r, enc), forER, autoCloseInput);
    }
    
    protected XMLStreamReader2 createSR(final ReaderConfig cfg, final URL src, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        final SystemId systemId = SystemId.construct(src);
        try {
            return this.createSR(cfg, systemId, URLUtil.inputStreamFromURL(src), forER, autoCloseInput);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    private XMLStreamReader2 createSR(final ReaderConfig cfg, final SystemId systemId, final InputStream in, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        return this.doCreateSR(cfg, systemId, StreamBootstrapper.getInstance(null, systemId, in), forER, autoCloseInput);
    }
    
    protected XMLStreamReader2 createSR(final SystemId systemId, final Reader r, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        return this.createSR(this.createPrivateConfig(), systemId, ReaderBootstrapper.getInstance(null, systemId, r, null), forER, autoCloseInput);
    }
    
    protected XMLStreamReader2 createSR(final File f, final boolean forER, final boolean autoCloseInput) throws XMLStreamException {
        final ReaderConfig cfg = this.createPrivateConfig();
        try {
            if (!f.isAbsolute()) {
                final URL base = cfg.getBaseURL();
                if (base != null) {
                    final URL src = new URL(base, f.getPath());
                    return this.createSR(cfg, SystemId.construct(src), URLUtil.inputStreamFromURL(src), forER, autoCloseInput);
                }
            }
            final SystemId systemId = SystemId.construct(URLUtil.toURL(f));
            return this.createSR(cfg, systemId, new FileInputStream(f), forER, autoCloseInput);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }
    
    protected XMLStreamReader2 createSR(final Source src, final boolean forER) throws XMLStreamException {
        final ReaderConfig cfg = this.createPrivateConfig();
        Reader r = null;
        InputStream in = null;
        String pubId = null;
        String sysId = null;
        String encoding = null;
        InputBootstrapper bs = null;
        boolean autoCloseInput;
        if (src instanceof Stax2Source) {
            final Stax2Source ss = (Stax2Source)src;
            sysId = ss.getSystemId();
            pubId = ss.getPublicId();
            encoding = ss.getEncoding();
            try {
                if (src instanceof Stax2ByteArraySource) {
                    final Stax2ByteArraySource bas = (Stax2ByteArraySource)src;
                    bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), bas.getBuffer(), bas.getBufferStart(), bas.getBufferEnd());
                }
                else {
                    in = ss.constructInputStream();
                    if (in == null) {
                        r = ss.constructReader();
                    }
                }
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            autoCloseInput = true;
        }
        else if (src instanceof StreamSource) {
            final StreamSource ss2 = (StreamSource)src;
            sysId = ss2.getSystemId();
            pubId = ss2.getPublicId();
            in = ss2.getInputStream();
            if (in == null) {
                r = ss2.getReader();
            }
            autoCloseInput = cfg.willAutoCloseInput();
        }
        else if (src instanceof SAXSource) {
            final SAXSource ss3 = (SAXSource)src;
            sysId = ss3.getSystemId();
            final InputSource isrc = ss3.getInputSource();
            if (isrc != null) {
                encoding = isrc.getEncoding();
                in = isrc.getByteStream();
                if (in == null) {
                    r = isrc.getCharacterStream();
                }
            }
            autoCloseInput = cfg.willAutoCloseInput();
        }
        else {
            if (src instanceof DOMSource) {
                final DOMSource domSrc = (DOMSource)src;
                return WstxDOMWrappingReader.createFrom(domSrc, cfg);
            }
            throw new IllegalArgumentException("Can not instantiate Stax reader for XML source type " + src.getClass() + " (unrecognized type)");
        }
        if (bs == null) {
            if (r != null) {
                bs = ReaderBootstrapper.getInstance(pubId, SystemId.construct(sysId), r, encoding);
            }
            else {
                if (in == null) {
                    if (sysId != null && sysId.length() > 0) {
                        autoCloseInput = true;
                        try {
                            return this.createSR(cfg, URLUtil.urlFromSystemId(sysId), forER, autoCloseInput);
                        }
                        catch (IOException ioe2) {
                            throw new WstxIOException(ioe2);
                        }
                    }
                    throw new XMLStreamException("Can not create Stax reader for the Source passed -- neither reader, input stream nor system id was accessible; can not use other types of sources (like embedded SAX streams)");
                }
                bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), in);
            }
        }
        return this.createSR(cfg, sysId, bs, forER, autoCloseInput);
    }
    
    protected XMLEventAllocator createEventAllocator() {
        if (this.mAllocator != null) {
            return this.mAllocator.newInstance();
        }
        return this.mConfig.willPreserveLocation() ? DefaultEventAllocator.getDefaultInstance() : DefaultEventAllocator.getFastInstance();
    }
    
    public ReaderConfig createPrivateConfig() {
        return this.mConfig.createNonShared(this.mSymbols.makeChild());
    }
    
    static {
        (mRootSymbols = DefaultXmlSymbolTable.getInstance()).setInternStrings(true);
    }
}
