// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.compat.QNameCreator;
import javax.xml.namespace.QName;
import com.ctc.wstx.util.BijectiveNsMap;
import java.util.HashSet;

public final class SimpleOutputElement extends OutputElementBase
{
    protected SimpleOutputElement mParent;
    protected String mPrefix;
    protected String mLocalName;
    protected String mURI;
    protected HashSet<AttrName> mAttrSet;
    
    private SimpleOutputElement() {
        this.mAttrSet = null;
        this.mParent = null;
        this.mPrefix = null;
        this.mLocalName = "";
        this.mURI = null;
    }
    
    private SimpleOutputElement(final SimpleOutputElement parent, final String prefix, final String localName, final String uri, final BijectiveNsMap ns) {
        super(parent, ns);
        this.mAttrSet = null;
        this.mParent = parent;
        this.mPrefix = prefix;
        this.mLocalName = localName;
        this.mURI = uri;
    }
    
    private void relink(final SimpleOutputElement parent, final String prefix, final String localName, final String uri) {
        super.relink(parent);
        this.mParent = parent;
        this.mPrefix = prefix;
        this.mLocalName = localName;
        this.mURI = uri;
        this.mNsMapping = parent.mNsMapping;
        this.mNsMapShared = (this.mNsMapping != null);
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
    }
    
    public static SimpleOutputElement createRoot() {
        return new SimpleOutputElement();
    }
    
    protected SimpleOutputElement createChild(final String localName) {
        this.mAttrSet = null;
        return new SimpleOutputElement(this, null, localName, this.mDefaultNsURI, this.mNsMapping);
    }
    
    protected SimpleOutputElement reuseAsChild(final SimpleOutputElement parent, final String localName) {
        this.mAttrSet = null;
        final SimpleOutputElement poolHead = this.mParent;
        this.relink(parent, null, localName, this.mDefaultNsURI);
        return poolHead;
    }
    
    protected SimpleOutputElement reuseAsChild(final SimpleOutputElement parent, final String prefix, final String localName, final String uri) {
        this.mAttrSet = null;
        final SimpleOutputElement poolHead = this.mParent;
        this.relink(parent, prefix, localName, uri);
        return poolHead;
    }
    
    protected SimpleOutputElement createChild(final String prefix, final String localName, final String uri) {
        this.mAttrSet = null;
        return new SimpleOutputElement(this, prefix, localName, uri, this.mNsMapping);
    }
    
    protected void addToPool(final SimpleOutputElement poolHead) {
        this.mParent = poolHead;
    }
    
    public SimpleOutputElement getParent() {
        return this.mParent;
    }
    
    @Override
    public boolean isRoot() {
        return this.mParent == null;
    }
    
    @Override
    public String getNameDesc() {
        if (this.mPrefix != null && this.mPrefix.length() > 0) {
            return this.mPrefix + ":" + this.mLocalName;
        }
        if (this.mLocalName != null && this.mLocalName.length() > 0) {
            return this.mLocalName;
        }
        return "#error";
    }
    
    public String getPrefix() {
        return this.mPrefix;
    }
    
    public String getLocalName() {
        return this.mLocalName;
    }
    
    public String getNamespaceURI() {
        return this.mURI;
    }
    
    public QName getName() {
        return QNameCreator.create(this.mURI, this.mLocalName, this.mPrefix);
    }
    
    public void checkAttrWrite(final String nsURI, final String localName) throws XMLStreamException {
        final AttrName an = new AttrName(nsURI, localName);
        if (this.mAttrSet == null) {
            this.mAttrSet = new HashSet<AttrName>();
        }
        if (!this.mAttrSet.add(an)) {
            throw new XMLStreamException("Duplicate attribute write for attribute '" + an + "'");
        }
    }
    
    public void setPrefix(final String prefix) {
        this.mPrefix = prefix;
    }
    
    @Override
    public void setDefaultNsUri(final String uri) {
        this.mDefaultNsURI = uri;
    }
    
    @Override
    protected final void setRootNsContext(final NamespaceContext ctxt) {
        this.mRootNsContext = ctxt;
        final String defURI = ctxt.getNamespaceURI("");
        if (defURI != null && defURI.length() > 0) {
            this.mDefaultNsURI = defURI;
        }
    }
    
    static final class AttrName implements Comparable<AttrName>
    {
        final String mNsURI;
        final String mLocalName;
        final int mHashCode;
        
        public AttrName(final String nsURI, final String localName) {
            this.mNsURI = ((nsURI == null) ? "" : nsURI);
            this.mLocalName = localName;
            this.mHashCode = (this.mNsURI.hashCode() * 31 ^ this.mLocalName.hashCode());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof AttrName)) {
                return false;
            }
            final AttrName other = (AttrName)o;
            final String otherLN = other.mLocalName;
            if (otherLN != this.mLocalName && !otherLN.equals(this.mLocalName)) {
                return false;
            }
            final String otherURI = other.mNsURI;
            return otherURI == this.mNsURI || otherURI.equals(this.mNsURI);
        }
        
        @Override
        public String toString() {
            if (this.mNsURI.length() > 0) {
                return "{" + this.mNsURI + "} " + this.mLocalName;
            }
            return this.mLocalName;
        }
        
        @Override
        public int hashCode() {
            return this.mHashCode;
        }
        
        @Override
        public int compareTo(final AttrName other) {
            int result = this.mNsURI.compareTo(other.mNsURI);
            if (result == 0) {
                result = this.mLocalName.compareTo(other.mLocalName);
            }
            return result;
        }
    }
}
