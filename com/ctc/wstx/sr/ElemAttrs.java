// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import javax.xml.namespace.QName;

public final class ElemAttrs
{
    private static final int OFFSET_NS_URI = 1;
    private final String[] mRawAttrs;
    private final int mDefaultOffset;
    private final int[] mAttrMap;
    private final int mAttrHashSize;
    private final int mAttrSpillEnd;
    
    public ElemAttrs(final String[] rawAttrs, final int defOffset) {
        this.mRawAttrs = rawAttrs;
        this.mAttrMap = null;
        this.mAttrHashSize = 0;
        this.mAttrSpillEnd = 0;
        this.mDefaultOffset = defOffset << 2;
    }
    
    public ElemAttrs(final String[] rawAttrs, final int defOffset, final int[] attrMap, final int hashSize, final int spillEnd) {
        this.mRawAttrs = rawAttrs;
        this.mDefaultOffset = defOffset << 2;
        this.mAttrMap = attrMap;
        this.mAttrHashSize = hashSize;
        this.mAttrSpillEnd = spillEnd;
    }
    
    public String[] getRawAttrs() {
        return this.mRawAttrs;
    }
    
    public int findIndex(final QName name) {
        if (this.mAttrMap != null) {
            return this.findMapIndex(name.getNamespaceURI(), name.getLocalPart());
        }
        final String ln = name.getLocalPart();
        final String uri = name.getNamespaceURI();
        final boolean defaultNs = uri == null || uri.length() == 0;
        final String[] raw = this.mRawAttrs;
        for (int i = 0, len = raw.length; i < len; i += 4) {
            if (ln.equals(raw[i])) {
                final String thisUri = raw[i + 1];
                if (defaultNs) {
                    if (thisUri == null || thisUri.length() == 0) {
                        return i;
                    }
                }
                else if (thisUri != null && (thisUri == uri || thisUri.equals(uri))) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getFirstDefaultOffset() {
        return this.mDefaultOffset;
    }
    
    public boolean isDefault(final int ix) {
        return ix >= this.mDefaultOffset;
    }
    
    private final int findMapIndex(String nsURI, final String localName) {
        int hash = localName.hashCode();
        if (nsURI == null) {
            nsURI = "";
        }
        else if (nsURI.length() > 0) {
            hash ^= nsURI.hashCode();
        }
        int ix = this.mAttrMap[hash & this.mAttrHashSize - 1];
        if (ix == 0) {
            return -1;
        }
        ix = ix - 1 << 2;
        final String[] raw = this.mRawAttrs;
        String thisName = raw[ix];
        if (thisName == localName || thisName.equals(localName)) {
            final String thisURI = raw[ix + 1];
            if (thisURI == nsURI) {
                return ix;
            }
            if (thisURI == null) {
                if (nsURI.length() == 0) {
                    return ix;
                }
            }
            else if (thisURI.equals(nsURI)) {
                return ix;
            }
        }
        for (int i = this.mAttrHashSize, len = this.mAttrSpillEnd; i < len; i += 2) {
            if (this.mAttrMap[i] == hash) {
                ix = this.mAttrMap[i + 1] << 2;
                thisName = raw[ix];
                if (thisName == localName || thisName.equals(localName)) {
                    final String thisURI2 = raw[ix + 1];
                    if (thisURI2 == nsURI) {
                        return ix;
                    }
                    if (thisURI2 == null) {
                        if (nsURI.length() == 0) {
                            return ix;
                        }
                    }
                    else if (thisURI2.equals(nsURI)) {
                        return ix;
                    }
                }
            }
        }
        return -1;
    }
}
