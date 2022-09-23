// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import com.ctc.wstx.compat.QNameCreator;
import javax.xml.namespace.QName;

final class Attribute
{
    protected String mLocalName;
    protected String mPrefix;
    protected String mNamespaceURI;
    protected int mValueStartOffset;
    protected String mReusableValue;
    
    public Attribute(final String prefix, final String localName, final int valueStart) {
        this.mLocalName = localName;
        this.mPrefix = prefix;
        this.mValueStartOffset = valueStart;
    }
    
    public void reset(final String prefix, final String localName, final int valueStart) {
        this.mLocalName = localName;
        this.mPrefix = prefix;
        this.mValueStartOffset = valueStart;
        this.mNamespaceURI = null;
        this.mReusableValue = null;
    }
    
    public void setValue(final String value) {
        this.mReusableValue = value;
    }
    
    protected boolean hasQName(final String uri, final String localName) {
        if (localName != this.mLocalName && !localName.equals(this.mLocalName)) {
            return false;
        }
        if (this.mNamespaceURI == uri) {
            return true;
        }
        if (uri == null) {
            return this.mNamespaceURI == null || this.mNamespaceURI.length() == 0;
        }
        return this.mNamespaceURI != null && uri.equals(this.mNamespaceURI);
    }
    
    public QName getQName() {
        if (this.mPrefix != null) {
            String uri = this.mNamespaceURI;
            if (uri == null) {
                uri = "";
            }
            return QNameCreator.create(uri, this.mLocalName, this.mPrefix);
        }
        if (this.mNamespaceURI == null) {
            return new QName(this.mLocalName);
        }
        return new QName(this.mNamespaceURI, this.mLocalName);
    }
    
    public String getValue(final String allValues) {
        if (this.mReusableValue == null) {
            this.mReusableValue = ((this.mValueStartOffset == 0) ? allValues : allValues.substring(this.mValueStartOffset));
        }
        return this.mReusableValue;
    }
    
    public String getValue(final String allValues, final int endOffset) {
        if (this.mReusableValue == null) {
            this.mReusableValue = allValues.substring(this.mValueStartOffset, endOffset);
        }
        return this.mReusableValue;
    }
}
