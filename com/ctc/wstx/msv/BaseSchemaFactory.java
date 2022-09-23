// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.msv;

import org.xml.sax.Locator;
import com.sun.msv.reader.util.IgnoreController;
import java.io.File;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.util.URLUtil;
import java.net.URL;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.InputSource;
import org.codehaus.stax2.validation.XMLValidationSchema;
import java.io.InputStream;
import com.ctc.wstx.api.ValidatorConfig;
import javax.xml.parsers.SAXParserFactory;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

public abstract class BaseSchemaFactory extends XMLValidationSchemaFactory
{
    protected static SAXParserFactory sSaxFactory;
    protected final ValidatorConfig mConfig;
    
    protected BaseSchemaFactory(final String schemaType) {
        super(schemaType);
        this.mConfig = ValidatorConfig.createDefaults();
    }
    
    @Override
    public boolean isPropertySupported(final String propName) {
        return this.mConfig.isPropertySupported(propName);
    }
    
    @Override
    public boolean setProperty(final String propName, final Object value) {
        return this.mConfig.setProperty(propName, value);
    }
    
    @Override
    public Object getProperty(final String propName) {
        return this.mConfig.getProperty(propName);
    }
    
    @Override
    public XMLValidationSchema createSchema(final InputStream in, final String encoding, final String publicId, final String systemId) throws XMLStreamException {
        final InputSource src = new InputSource(in);
        src.setEncoding(encoding);
        src.setPublicId(publicId);
        src.setSystemId(systemId);
        return this.loadSchema(src, systemId);
    }
    
    @Override
    public XMLValidationSchema createSchema(final Reader r, final String publicId, final String systemId) throws XMLStreamException {
        final InputSource src = new InputSource(r);
        src.setPublicId(publicId);
        src.setSystemId(systemId);
        return this.loadSchema(src, systemId);
    }
    
    @Override
    public XMLValidationSchema createSchema(final URL url) throws XMLStreamException {
        try {
            final InputStream in = URLUtil.inputStreamFromURL(url);
            final InputSource src = new InputSource(in);
            src.setSystemId(url.toExternalForm());
            return this.loadSchema(src, url);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public XMLValidationSchema createSchema(final File f) throws XMLStreamException {
        try {
            return this.createSchema(f.toURL());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected abstract XMLValidationSchema loadSchema(final InputSource p0, final Object p1) throws XMLStreamException;
    
    protected static synchronized SAXParserFactory getSaxFactory() {
        if (BaseSchemaFactory.sSaxFactory == null) {
            (BaseSchemaFactory.sSaxFactory = SAXParserFactory.newInstance()).setNamespaceAware(true);
        }
        return BaseSchemaFactory.sSaxFactory;
    }
    
    static final class MyGrammarController extends IgnoreController
    {
        public String mErrorMsg;
        
        public MyGrammarController() {
            this.mErrorMsg = null;
        }
        
        public void error(final Locator[] locs, final String msg, final Exception nestedException) {
            if (this.mErrorMsg == null) {
                this.mErrorMsg = msg;
            }
            else {
                this.mErrorMsg = this.mErrorMsg + "; " + msg;
            }
        }
    }
}
