// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import com.ctc.wstx.util.DataUtil;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import javax.xml.stream.Location;
import com.ctc.wstx.util.BaseNsContext;

public final class CompactNsContext extends BaseNsContext
{
    final Location mLocation;
    final String[] mNamespaces;
    final int mNsLength;
    final int mFirstLocalNs;
    transient ArrayList<Namespace> mNsList;
    
    public CompactNsContext(final Location loc, final String[] namespaces, final int nsLen, final int firstLocal) {
        this.mLocation = loc;
        this.mNamespaces = namespaces;
        this.mNsLength = nsLen;
        this.mFirstLocalNs = firstLocal;
    }
    
    @Override
    public String doGetNamespaceURI(final String prefix) {
        final String[] ns = this.mNamespaces;
        if (prefix.length() == 0) {
            for (int i = this.mNsLength - 2; i >= 0; i -= 2) {
                if (ns[i] == null) {
                    return ns[i + 1];
                }
            }
            return null;
        }
        for (int i = this.mNsLength - 2; i >= 0; i -= 2) {
            if (prefix.equals(ns[i])) {
                return ns[i + 1];
            }
        }
        return null;
    }
    
    @Override
    public String doGetPrefix(final String nsURI) {
        final String[] ns = this.mNamespaces;
        final int len = this.mNsLength;
    Label_0090:
        for (int i = len - 1; i > 0; i -= 2) {
            if (nsURI.equals(ns[i])) {
                final String prefix = ns[i - 1];
                for (int j = i + 1; j < len; j += 2) {
                    if (ns[j] == prefix) {
                        continue Label_0090;
                    }
                }
                final String uri = ns[i - 1];
                return (uri == null) ? "" : uri;
            }
        }
        return null;
    }
    
    @Override
    public Iterator<String> doGetPrefixes(final String nsURI) {
        final String[] ns = this.mNamespaces;
        final int len = this.mNsLength;
        String first = null;
        ArrayList<String> all = null;
    Label_0136:
        for (int i = len - 1; i > 0; i -= 2) {
            final String currNS = ns[i];
            if (currNS == nsURI || currNS.equals(nsURI)) {
                String prefix = ns[i - 1];
                for (int j = i + 1; j < len; j += 2) {
                    if (ns[j] == prefix) {
                        continue Label_0136;
                    }
                }
                if (prefix == null) {
                    prefix = "";
                }
                if (first == null) {
                    first = prefix;
                }
                else {
                    if (all == null) {
                        all = new ArrayList<String>();
                        all.add(first);
                    }
                    all.add(prefix);
                }
            }
        }
        if (all != null) {
            return all.iterator();
        }
        if (first != null) {
            return DataUtil.singletonIterator(first);
        }
        return DataUtil.emptyIterator();
    }
    
    @Override
    public Iterator<Namespace> getNamespaces() {
        if (this.mNsList == null) {
            int firstLocal = this.mFirstLocalNs;
            int len = this.mNsLength - firstLocal;
            if (len == 0) {
                return DataUtil.emptyIterator();
            }
            if (len == 2) {
                return (Iterator<Namespace>)DataUtil.singletonIterator(NamespaceEventImpl.constructNamespace(this.mLocation, this.mNamespaces[firstLocal], this.mNamespaces[firstLocal + 1]));
            }
            final ArrayList<Namespace> l = new ArrayList<Namespace>(len >> 1);
            final String[] ns = this.mNamespaces;
            for (len = this.mNsLength; firstLocal < len; firstLocal += 2) {
                l.add(NamespaceEventImpl.constructNamespace(this.mLocation, ns[firstLocal], ns[firstLocal + 1]));
            }
            this.mNsList = l;
        }
        return this.mNsList.iterator();
    }
    
    @Override
    public void outputNamespaceDeclarations(final Writer w) throws IOException {
        final String[] ns = this.mNamespaces;
        for (int i = this.mFirstLocalNs, len = this.mNsLength; i < len; i += 2) {
            w.write(32);
            w.write("xmlns");
            final String prefix = ns[i];
            if (prefix != null && prefix.length() > 0) {
                w.write(58);
                w.write(prefix);
            }
            w.write("=\"");
            w.write(ns[i + 1]);
            w.write(34);
        }
    }
    
    @Override
    public void outputNamespaceDeclarations(final XMLStreamWriter w) throws XMLStreamException {
        final String[] ns = this.mNamespaces;
        for (int i = this.mFirstLocalNs, len = this.mNsLength; i < len; i += 2) {
            final String nsURI = ns[i + 1];
            final String prefix = ns[i];
            if (prefix != null && prefix.length() > 0) {
                w.writeNamespace(prefix, nsURI);
            }
            else {
                w.writeDefaultNamespace(nsURI);
            }
        }
    }
}
