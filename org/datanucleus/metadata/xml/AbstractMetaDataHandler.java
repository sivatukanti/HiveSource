// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.xml;

import org.datanucleus.ClassConstants;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.datanucleus.util.NucleusLogger;
import org.xml.sax.SAXParseException;
import java.util.Stack;
import org.xml.sax.EntityResolver;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.util.Localiser;
import org.xml.sax.helpers.DefaultHandler;

public class AbstractMetaDataHandler extends DefaultHandler
{
    protected static final Localiser LOCALISER;
    protected final MetaDataManager mgr;
    protected final String filename;
    protected MetaData metadata;
    protected final EntityResolver entityResolver;
    protected StringBuffer charactersBuffer;
    protected boolean validate;
    protected Stack<MetaData> stack;
    
    public AbstractMetaDataHandler(final MetaDataManager mgr, final String filename, final EntityResolver resolver) {
        this.charactersBuffer = new StringBuffer();
        this.validate = true;
        this.stack = new Stack<MetaData>();
        this.mgr = mgr;
        this.filename = filename;
        this.entityResolver = resolver;
    }
    
    public void setValidate(final boolean validate) {
        this.validate = validate;
    }
    
    public MetaData getMetaData() {
        return this.metadata;
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        if (this.validate) {
            if (e.getColumnNumber() >= 0) {
                NucleusLogger.METADATA.warn(AbstractMetaDataHandler.LOCALISER.msg("044039", this.filename, "" + e.getLineNumber(), "" + e.getColumnNumber(), e.getMessage()));
            }
            else {
                NucleusLogger.METADATA.warn(AbstractMetaDataHandler.LOCALISER.msg("044038", this.filename, "" + e.getLineNumber(), e.getMessage()));
            }
        }
    }
    
    protected String getAttr(final Attributes attrs, final String key, final String defaultValue) {
        final String result = attrs.getValue(key);
        if (result == null) {
            return defaultValue;
        }
        if (result.length() == 0) {
            return defaultValue;
        }
        return result;
    }
    
    protected String getAttr(final Attributes attrs, final String key) {
        return this.getAttr(attrs, key, null);
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        InputSource source = null;
        if (this.entityResolver != null) {
            try {
                source = this.entityResolver.resolveEntity(publicId, systemId);
            }
            catch (IOException ex) {}
        }
        if (source == null) {
            try {
                return super.resolveEntity(publicId, systemId);
            }
            catch (IOException ex2) {}
        }
        return source;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.charactersBuffer.append(ch, start, length);
    }
    
    public String getString() {
        final String result = this.charactersBuffer.toString();
        this.charactersBuffer = new StringBuffer();
        return result;
    }
    
    protected MetaData getStack() {
        return this.stack.lastElement();
    }
    
    protected MetaData popStack() {
        return this.stack.pop();
    }
    
    protected void pushStack(final MetaData md) {
        this.stack.push(md);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
