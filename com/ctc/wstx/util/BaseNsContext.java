// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import com.ctc.wstx.cfg.ErrorConsts;
import javax.xml.namespace.NamespaceContext;

public abstract class BaseNsContext implements NamespaceContext
{
    protected static final String UNDECLARED_NS_URI = "";
    
    @Override
    public final String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
        }
        if (prefix.length() > 0) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
        }
        return this.doGetNamespaceURI(prefix);
    }
    
    @Override
    public final String getPrefix(final String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return this.doGetPrefix(nsURI);
    }
    
    @Override
    public final Iterator<String> getPrefixes(final String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return DataUtil.singletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return DataUtil.singletonIterator("xmlns");
        }
        return this.doGetPrefixes(nsURI);
    }
    
    public abstract Iterator<Namespace> getNamespaces();
    
    public abstract void outputNamespaceDeclarations(final Writer p0) throws IOException;
    
    public abstract void outputNamespaceDeclarations(final XMLStreamWriter p0) throws XMLStreamException;
    
    public abstract String doGetNamespaceURI(final String p0);
    
    public abstract String doGetPrefix(final String p0);
    
    public abstract Iterator<String> doGetPrefixes(final String p0);
}
