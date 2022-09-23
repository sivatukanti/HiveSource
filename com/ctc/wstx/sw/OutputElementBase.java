// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import java.util.List;
import com.ctc.wstx.util.DataUtil;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.BijectiveNsMap;
import javax.xml.namespace.NamespaceContext;

public abstract class OutputElementBase implements NamespaceContext
{
    public static final int PREFIX_UNBOUND = 0;
    public static final int PREFIX_OK = 1;
    public static final int PREFIX_MISBOUND = 2;
    static final String sXmlNsPrefix = "xml";
    static final String sXmlNsURI = "http://www.w3.org/XML/1998/namespace";
    protected NamespaceContext mRootNsContext;
    protected String mDefaultNsURI;
    protected BijectiveNsMap mNsMapping;
    protected boolean mNsMapShared;
    
    protected OutputElementBase() {
        this.mNsMapping = null;
        this.mNsMapShared = false;
        this.mDefaultNsURI = "";
        this.mRootNsContext = null;
    }
    
    protected OutputElementBase(final OutputElementBase parent, final BijectiveNsMap ns) {
        this.mNsMapping = ns;
        this.mNsMapShared = (ns != null);
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
    }
    
    protected void relink(final OutputElementBase parent) {
        this.mNsMapping = parent.mNsMapping;
        this.mNsMapShared = (this.mNsMapping != null);
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
    }
    
    protected abstract void setRootNsContext(final NamespaceContext p0);
    
    public abstract boolean isRoot();
    
    public abstract String getNameDesc();
    
    public final String getDefaultNsUri() {
        return this.mDefaultNsURI;
    }
    
    public final String getExplicitPrefix(final String uri) {
        if (this.mNsMapping != null) {
            final String prefix = this.mNsMapping.findPrefixByUri(uri);
            if (prefix != null) {
                return prefix;
            }
        }
        if (this.mRootNsContext != null) {
            final String prefix = this.mRootNsContext.getPrefix(uri);
            if (prefix != null && prefix.length() > 0) {
                return prefix;
            }
        }
        return null;
    }
    
    public final int isPrefixValid(final String prefix, String nsURI, final boolean isElement) throws XMLStreamException {
        if (nsURI == null) {
            nsURI = "";
        }
        if (prefix == null || prefix.length() == 0) {
            if (isElement) {
                if (nsURI == this.mDefaultNsURI || nsURI.equals(this.mDefaultNsURI)) {
                    return 1;
                }
            }
            else if (nsURI.length() == 0) {
                return 1;
            }
            return 2;
        }
        if (prefix.equals("xml")) {
            if (!nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
                this.throwOutputError("Namespace prefix 'xml' can not be bound to non-default namespace ('" + nsURI + "'); has to be the default '" + "http://www.w3.org/XML/1998/namespace" + "'");
            }
            return 1;
        }
        String act;
        if (this.mNsMapping != null) {
            act = this.mNsMapping.findUriByPrefix(prefix);
        }
        else {
            act = null;
        }
        if (act == null && this.mRootNsContext != null) {
            act = this.mRootNsContext.getNamespaceURI(prefix);
        }
        if (act == null) {
            return 0;
        }
        return (act == nsURI || act.equals(nsURI)) ? 1 : 2;
    }
    
    public abstract void setDefaultNsUri(final String p0);
    
    public final String generateMapping(final String prefixBase, final String uri, final int[] seqArr) {
        if (this.mNsMapping == null) {
            this.mNsMapping = BijectiveNsMap.createEmpty();
        }
        else if (this.mNsMapShared) {
            this.mNsMapping = this.mNsMapping.createChild();
            this.mNsMapShared = false;
        }
        return this.mNsMapping.addGeneratedMapping(prefixBase, this.mRootNsContext, uri, seqArr);
    }
    
    public final void addPrefix(final String prefix, final String uri) {
        if (this.mNsMapping == null) {
            this.mNsMapping = BijectiveNsMap.createEmpty();
        }
        else if (this.mNsMapShared) {
            this.mNsMapping = this.mNsMapping.createChild();
            this.mNsMapShared = false;
        }
        this.mNsMapping.addMapping(prefix, uri);
    }
    
    @Override
    public final String getNamespaceURI(final String prefix) {
        if (prefix.length() == 0) {
            return this.mDefaultNsURI;
        }
        if (this.mNsMapping != null) {
            final String uri = this.mNsMapping.findUriByPrefix(prefix);
            if (uri != null) {
                return uri;
            }
        }
        return (this.mRootNsContext != null) ? this.mRootNsContext.getNamespaceURI(prefix) : null;
    }
    
    @Override
    public final String getPrefix(final String uri) {
        if (this.mDefaultNsURI.equals(uri)) {
            return "";
        }
        if (this.mNsMapping != null) {
            final String prefix = this.mNsMapping.findPrefixByUri(uri);
            if (prefix != null) {
                return prefix;
            }
        }
        return (this.mRootNsContext != null) ? this.mRootNsContext.getPrefix(uri) : null;
    }
    
    @Override
    public final Iterator<String> getPrefixes(final String uri) {
        List<String> l = null;
        if (this.mDefaultNsURI.equals(uri)) {
            l = new ArrayList<String>();
            l.add("");
        }
        if (this.mNsMapping != null) {
            l = this.mNsMapping.getPrefixesBoundToUri(uri, l);
        }
        if (this.mRootNsContext != null) {
            final Iterator<?> it = this.mRootNsContext.getPrefixes(uri);
            while (it.hasNext()) {
                final String prefix = (String)it.next();
                if (prefix.length() == 0) {
                    continue;
                }
                if (l == null) {
                    l = new ArrayList<String>();
                }
                else if (l.contains(prefix)) {
                    continue;
                }
                l.add(prefix);
            }
        }
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }
    
    protected final void throwOutputError(final String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }
}
