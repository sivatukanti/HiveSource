// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import java.util.LinkedHashMap;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import com.ctc.wstx.util.DataUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import javax.xml.stream.events.Namespace;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import com.ctc.wstx.util.BaseNsContext;

public class MergedNsContext extends BaseNsContext
{
    final NamespaceContext mParentCtxt;
    final List<Namespace> mNamespaces;
    Map<String, Namespace> mNsByPrefix;
    Map<String, Namespace> mNsByURI;
    
    protected MergedNsContext(final NamespaceContext parentCtxt, final List<Namespace> localNs) {
        this.mNsByPrefix = null;
        this.mNsByURI = null;
        this.mParentCtxt = parentCtxt;
        if (localNs == null) {
            this.mNamespaces = Collections.emptyList();
        }
        else {
            this.mNamespaces = localNs;
        }
    }
    
    public static BaseNsContext construct(final NamespaceContext parentCtxt, final List<Namespace> localNs) {
        return new MergedNsContext(parentCtxt, localNs);
    }
    
    @Override
    public String doGetNamespaceURI(final String prefix) {
        if (this.mNsByPrefix == null) {
            this.mNsByPrefix = this.buildByPrefixMap();
        }
        final Namespace ns = this.mNsByPrefix.get(prefix);
        if (ns == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getNamespaceURI(prefix);
        }
        return (ns == null) ? null : ns.getNamespaceURI();
    }
    
    @Override
    public String doGetPrefix(final String nsURI) {
        if (this.mNsByURI == null) {
            this.mNsByURI = this.buildByNsURIMap();
        }
        final Namespace ns = this.mNsByURI.get(nsURI);
        if (ns == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getPrefix(nsURI);
        }
        return (ns == null) ? null : ns.getPrefix();
    }
    
    @Override
    public Iterator<String> doGetPrefixes(final String nsURI) {
        ArrayList<String> l = null;
        for (int i = 0, len = this.mNamespaces.size(); i < len; ++i) {
            final Namespace ns = this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            if (uri.equals(nsURI)) {
                if (l == null) {
                    l = new ArrayList<String>();
                }
                final String prefix = ns.getPrefix();
                l.add((prefix == null) ? "" : prefix);
            }
        }
        if (this.mParentCtxt != null) {
            final Iterator<String> it = this.mParentCtxt.getPrefixes(nsURI);
            if (l == null) {
                return it;
            }
            while (it.hasNext()) {
                l.add(it.next());
            }
        }
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }
    
    @Override
    public Iterator<Namespace> getNamespaces() {
        return this.mNamespaces.iterator();
    }
    
    @Override
    public void outputNamespaceDeclarations(final Writer w) throws IOException {
        for (int i = 0, len = this.mNamespaces.size(); i < len; ++i) {
            final Namespace ns = this.mNamespaces.get(i);
            w.write(32);
            w.write("xmlns");
            if (!ns.isDefaultNamespaceDeclaration()) {
                w.write(58);
                w.write(ns.getPrefix());
            }
            w.write("=\"");
            w.write(ns.getNamespaceURI());
            w.write(34);
        }
    }
    
    @Override
    public void outputNamespaceDeclarations(final XMLStreamWriter w) throws XMLStreamException {
        for (int i = 0, len = this.mNamespaces.size(); i < len; ++i) {
            final Namespace ns = this.mNamespaces.get(i);
            if (ns.isDefaultNamespaceDeclaration()) {
                w.writeDefaultNamespace(ns.getNamespaceURI());
            }
            else {
                w.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
            }
        }
    }
    
    private Map<String, Namespace> buildByPrefixMap() {
        final int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.emptyMap();
        }
        final LinkedHashMap<String, Namespace> m = new LinkedHashMap<String, Namespace>(1 + len + (len >> 1));
        for (int i = 0; i < len; ++i) {
            final Namespace ns = this.mNamespaces.get(i);
            String prefix = ns.getPrefix();
            if (prefix == null) {
                prefix = "";
            }
            m.put(prefix, ns);
        }
        return m;
    }
    
    private Map<String, Namespace> buildByNsURIMap() {
        final int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.emptyMap();
        }
        final LinkedHashMap<String, Namespace> m = new LinkedHashMap<String, Namespace>(1 + len + (len >> 1));
        for (int i = 0; i < len; ++i) {
            final Namespace ns = this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            m.put(uri, ns);
        }
        return m;
    }
}
