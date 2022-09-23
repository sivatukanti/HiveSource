// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.NamespaceContext;

public class MergedNsContext implements NamespaceContext
{
    final NamespaceContext mParentCtxt;
    final List mNamespaces;
    
    protected MergedNsContext(final NamespaceContext mParentCtxt, final List list) {
        this.mParentCtxt = mParentCtxt;
        this.mNamespaces = ((list == null) ? Collections.EMPTY_LIST : list);
    }
    
    public static MergedNsContext construct(final NamespaceContext namespaceContext, final List list) {
        return new MergedNsContext(namespaceContext, list);
    }
    
    public String getNamespaceURI(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Illegal to pass null prefix");
        }
        for (int i = 0; i < this.mNamespaces.size(); ++i) {
            final Namespace namespace = this.mNamespaces.get(i);
            if (s.equals(namespace.getPrefix())) {
                return namespace.getNamespaceURI();
            }
        }
        if (this.mParentCtxt != null) {
            final String namespaceURI = this.mParentCtxt.getNamespaceURI(s);
            if (namespaceURI != null) {
                return namespaceURI;
            }
        }
        if (s.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (s.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return null;
    }
    
    public String getPrefix(final String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        for (int i = 0; i < this.mNamespaces.size(); ++i) {
            final Namespace namespace = this.mNamespaces.get(i);
            if (s.equals(namespace.getNamespaceURI())) {
                return namespace.getPrefix();
            }
        }
        if (this.mParentCtxt != null) {
            final String prefix = this.mParentCtxt.getPrefix(s);
            if (prefix != null && this.getNamespaceURI(prefix).equals(s)) {
                return prefix;
            }
            final Iterator<String> prefixes = this.mParentCtxt.getPrefixes(s);
            while (prefixes.hasNext()) {
                final String s2 = prefixes.next();
                if (!s2.equals(prefix) && this.getNamespaceURI(s2).equals(s)) {
                    return s2;
                }
            }
        }
        if (s.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (s.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return null;
    }
    
    public Iterator getPrefixes(final String anObject) {
        if (anObject == null || anObject.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        ArrayList list = null;
        for (int i = 0; i < this.mNamespaces.size(); ++i) {
            final Namespace namespace = this.mNamespaces.get(i);
            if (anObject.equals(namespace.getNamespaceURI())) {
                list = this.addToList(list, namespace.getPrefix());
            }
        }
        if (this.mParentCtxt != null) {
            final Iterator<String> prefixes = this.mParentCtxt.getPrefixes(anObject);
            while (prefixes.hasNext()) {
                final String s = prefixes.next();
                if (this.getNamespaceURI(s).equals(anObject)) {
                    list = this.addToList(list, s);
                }
            }
        }
        if (anObject.equals("http://www.w3.org/XML/1998/namespace")) {
            list = this.addToList(list, "xml");
        }
        if (anObject.equals("http://www.w3.org/2000/xmlns/")) {
            this.addToList(list, "xmlns");
        }
        return null;
    }
    
    protected ArrayList addToList(ArrayList list, final String e) {
        if (list == null) {
            list = new ArrayList<String>();
        }
        list.add(e);
        return list;
    }
}
