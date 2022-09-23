// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.stream.events.Namespace;
import java.util.Iterator;

public final class EmptyNamespaceContext extends BaseNsContext
{
    static final EmptyNamespaceContext sInstance;
    
    private EmptyNamespaceContext() {
    }
    
    public static EmptyNamespaceContext getInstance() {
        return EmptyNamespaceContext.sInstance;
    }
    
    @Override
    public Iterator<Namespace> getNamespaces() {
        return DataUtil.emptyIterator();
    }
    
    @Override
    public void outputNamespaceDeclarations(final Writer w) {
    }
    
    @Override
    public void outputNamespaceDeclarations(final XMLStreamWriter w) {
    }
    
    @Override
    public String doGetNamespaceURI(final String prefix) {
        return null;
    }
    
    @Override
    public String doGetPrefix(final String nsURI) {
        return null;
    }
    
    @Override
    public Iterator<String> doGetPrefixes(final String nsURI) {
        return DataUtil.emptyIterator();
    }
    
    static {
        sInstance = new EmptyNamespaceContext();
    }
}
