// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class EmptyNamespaceContext implements NamespaceContext
{
    static final EmptyNamespaceContext sInstance;
    
    private EmptyNamespaceContext() {
    }
    
    public static EmptyNamespaceContext getInstance() {
        return EmptyNamespaceContext.sInstance;
    }
    
    public final String getNamespaceURI(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (s.length() > 0) {
            if (s.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (s.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
        }
        return null;
    }
    
    public final String getPrefix(final String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty URI as argument.");
        }
        if (s.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (s.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return null;
    }
    
    public final Iterator getPrefixes(final String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (s.equals("http://www.w3.org/XML/1998/namespace")) {
            return new SingletonIterator("xml");
        }
        if (s.equals("http://www.w3.org/2000/xmlns/")) {
            return new SingletonIterator("xmlns");
        }
        return EmptyIterator.getInstance();
    }
    
    static {
        sInstance = new EmptyNamespaceContext();
    }
}
