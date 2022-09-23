// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;

public class NamespaceEventImpl extends AttributeEventImpl implements Namespace
{
    final String mPrefix;
    final String mURI;
    
    protected NamespaceEventImpl(final Location location, final String muri) {
        super(location, "xmlns", "http://www.w3.org/2000/xmlns/", null, muri, true);
        this.mPrefix = "";
        this.mURI = muri;
    }
    
    protected NamespaceEventImpl(final Location location, final String mPrefix, final String muri) {
        super(location, mPrefix, "http://www.w3.org/2000/xmlns/", "xmlns", muri, true);
        this.mPrefix = mPrefix;
        this.mURI = muri;
    }
    
    public static NamespaceEventImpl constructDefaultNamespace(final Location location, final String s) {
        return new NamespaceEventImpl(location, s);
    }
    
    public static NamespaceEventImpl constructNamespace(final Location location, final String s, final String s2) {
        if (s == null || s.length() == 0) {
            return new NamespaceEventImpl(location, s2);
        }
        return new NamespaceEventImpl(location, s, s2);
    }
    
    public String getNamespaceURI() {
        return this.mURI;
    }
    
    public String getPrefix() {
        return this.mPrefix;
    }
    
    public boolean isDefaultNamespaceDeclaration() {
        return this.mPrefix.length() == 0;
    }
    
    @Override
    public int getEventType() {
        return 13;
    }
    
    @Override
    public boolean isNamespace() {
        return true;
    }
}
