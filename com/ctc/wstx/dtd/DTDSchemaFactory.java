// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.io.InputSourceFactory;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.util.URLUtil;
import com.ctc.wstx.io.ReaderBootstrapper;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import java.net.URL;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import org.codehaus.stax2.validation.XMLValidationSchema;
import java.io.InputStream;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.util.SymbolTable;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

public class DTDSchemaFactory extends XMLValidationSchemaFactory
{
    static final SymbolTable mRootSymbols;
    protected final ValidatorConfig mSchemaConfig;
    protected final ReaderConfig mReaderConfig;
    
    public DTDSchemaFactory() {
        super("http://www.w3.org/XML/1998/namespace");
        this.mReaderConfig = ReaderConfig.createFullDefaults();
        this.mSchemaConfig = ValidatorConfig.createDefaults();
    }
    
    @Override
    public boolean isPropertySupported(final String propName) {
        return this.mSchemaConfig.isPropertySupported(propName);
    }
    
    @Override
    public boolean setProperty(final String propName, final Object value) {
        return this.mSchemaConfig.setProperty(propName, value);
    }
    
    @Override
    public Object getProperty(final String propName) {
        return this.mSchemaConfig.getProperty(propName);
    }
    
    @Override
    public XMLValidationSchema createSchema(final InputStream in, final String encoding, final String publicId, final String systemId) throws XMLStreamException {
        final ReaderConfig rcfg = this.createPrivateReaderConfig();
        return this.doCreateSchema(rcfg, StreamBootstrapper.getInstance(publicId, SystemId.construct(systemId), in), publicId, systemId, null);
    }
    
    @Override
    public XMLValidationSchema createSchema(final Reader r, final String publicId, final String systemId) throws XMLStreamException {
        final ReaderConfig rcfg = this.createPrivateReaderConfig();
        return this.doCreateSchema(rcfg, ReaderBootstrapper.getInstance(publicId, SystemId.construct(systemId), r, null), publicId, systemId, null);
    }
    
    @Override
    public XMLValidationSchema createSchema(final URL url) throws XMLStreamException {
        final ReaderConfig rcfg = this.createPrivateReaderConfig();
        try {
            final InputStream in = URLUtil.inputStreamFromURL(url);
            return this.doCreateSchema(rcfg, StreamBootstrapper.getInstance(null, null, in), null, url.toExternalForm(), url);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public XMLValidationSchema createSchema(final File f) throws XMLStreamException {
        final ReaderConfig rcfg = this.createPrivateReaderConfig();
        try {
            final URL url = URLUtil.toURL(f);
            return this.doCreateSchema(rcfg, StreamBootstrapper.getInstance(null, null, new FileInputStream(f)), null, url.toExternalForm(), url);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected XMLValidationSchema doCreateSchema(final ReaderConfig rcfg, final InputBootstrapper bs, final String publicId, final String systemIdStr, URL ctxt) throws XMLStreamException {
        try {
            final Reader r = bs.bootstrapInput(rcfg, false, 0);
            if (bs.declaredXml11()) {
                rcfg.enableXml11(true);
            }
            if (ctxt == null) {
                ctxt = URLUtil.urlFromCurrentDir();
            }
            final SystemId systemId = SystemId.construct(systemIdStr, ctxt);
            final WstxInputSource src = InputSourceFactory.constructEntitySource(rcfg, null, null, bs, publicId, systemId, 0, r);
            return FullDTDReader.readExternalSubset(src, rcfg, null, true, bs.getDeclaredVersion());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    private ReaderConfig createPrivateReaderConfig() {
        return this.mReaderConfig.createNonShared(DTDSchemaFactory.mRootSymbols.makeChild());
    }
    
    static {
        (mRootSymbols = DefaultXmlSymbolTable.getInstance()).setInternStrings(true);
    }
}
