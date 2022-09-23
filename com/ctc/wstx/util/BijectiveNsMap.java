// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import java.util.List;

public final class BijectiveNsMap
{
    static final int DEFAULT_ARRAY_SIZE = 32;
    final int mScopeStart;
    String[] mNsStrings;
    int mScopeEnd;
    
    private BijectiveNsMap(final int scopeStart, final String[] strs) {
        this.mScopeEnd = scopeStart;
        this.mScopeStart = scopeStart;
        this.mNsStrings = strs;
    }
    
    public static BijectiveNsMap createEmpty() {
        final String[] strs = new String[32];
        strs[0] = "xml";
        strs[1] = "http://www.w3.org/XML/1998/namespace";
        strs[2] = "xmlns";
        strs[3] = "http://www.w3.org/2000/xmlns/";
        return new BijectiveNsMap(4, strs);
    }
    
    public BijectiveNsMap createChild() {
        return new BijectiveNsMap(this.mScopeEnd, this.mNsStrings);
    }
    
    public String findUriByPrefix(final String prefix) {
        final String[] strs = this.mNsStrings;
        final int phash = prefix.hashCode();
        for (int ix = this.mScopeEnd - 2; ix >= 0; ix -= 2) {
            final String thisP = strs[ix];
            if (thisP == prefix || (thisP.hashCode() == phash && thisP.equals(prefix))) {
                return strs[ix + 1];
            }
        }
        return null;
    }
    
    public String findPrefixByUri(final String uri) {
        final String[] strs = this.mNsStrings;
        final int uhash = uri.hashCode();
    Label_0141:
        for (int ix = this.mScopeEnd - 1; ix > 0; ix -= 2) {
            final String thisU = strs[ix];
            if (thisU == uri || (thisU.hashCode() == uhash && thisU.equals(uri))) {
                final String prefix = strs[ix - 1];
                if (ix < this.mScopeStart) {
                    final int phash = prefix.hashCode();
                    for (int j = ix + 1, end = this.mScopeEnd; j < end; j += 2) {
                        final String thisP = strs[j];
                        if (thisP == prefix) {
                            continue Label_0141;
                        }
                        if (thisP.hashCode() == phash && thisP.equals(prefix)) {
                            continue Label_0141;
                        }
                    }
                }
                return prefix;
            }
        }
        return null;
    }
    
    public List<String> getPrefixesBoundToUri(final String uri, List<String> l) {
        final String[] strs = this.mNsStrings;
        final int uhash = uri.hashCode();
    Label_0161:
        for (int ix = this.mScopeEnd - 1; ix > 0; ix -= 2) {
            final String thisU = strs[ix];
            if (thisU == uri || (thisU.hashCode() == uhash && thisU.equals(uri))) {
                final String prefix = strs[ix - 1];
                if (ix < this.mScopeStart) {
                    final int phash = prefix.hashCode();
                    for (int j = ix + 1, end = this.mScopeEnd; j < end; j += 2) {
                        final String thisP = strs[j];
                        if (thisP == prefix) {
                            continue Label_0161;
                        }
                        if (thisP.hashCode() == phash && thisP.equals(prefix)) {
                            continue Label_0161;
                        }
                    }
                }
                if (l == null) {
                    l = new ArrayList<String>();
                }
                l.add(prefix);
            }
        }
        return l;
    }
    
    public int size() {
        return this.mScopeEnd >> 1;
    }
    
    public int localSize() {
        return this.mScopeEnd - this.mScopeStart >> 1;
    }
    
    public String addMapping(final String prefix, final String uri) {
        String[] strs = this.mNsStrings;
        final int phash = prefix.hashCode();
        for (int ix = this.mScopeStart, end = this.mScopeEnd; ix < end; ix += 2) {
            final String thisP = strs[ix];
            if (thisP == prefix || (thisP.hashCode() == phash && thisP.equals(prefix))) {
                final String old = strs[ix + 1];
                strs[ix + 1] = uri;
                return old;
            }
        }
        if (this.mScopeEnd >= strs.length) {
            strs = DataUtil.growArrayBy(strs, strs.length);
            this.mNsStrings = strs;
        }
        strs[this.mScopeEnd++] = prefix;
        strs[this.mScopeEnd++] = uri;
        return null;
    }
    
    public String addGeneratedMapping(final String prefixBase, final NamespaceContext ctxt, final String uri, final int[] seqArr) {
        String[] strs = this.mNsStrings;
        int seqNr = seqArr[0];
        String prefix = null;
        do {
            Label_0012: {
                prefix = (prefixBase + seqNr).intern();
            }
            ++seqNr;
            final int phash = prefix.hashCode();
            for (int ix = this.mScopeEnd - 2; ix >= 0; ix -= 2) {
                final String thisP = strs[ix];
                if (thisP == prefix) {
                    continue Label_0012;
                }
                if (thisP.hashCode() == phash && thisP.equals(prefix)) {
                    continue Label_0012;
                }
            }
        } while (ctxt != null && ctxt.getNamespaceURI(prefix) != null);
        seqArr[0] = seqNr;
        if (this.mScopeEnd >= strs.length) {
            strs = DataUtil.growArrayBy(strs, strs.length);
            this.mNsStrings = strs;
        }
        strs[this.mScopeEnd++] = prefix;
        strs[this.mScopeEnd++] = uri;
        return prefix;
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass().toString() + "; " + this.size() + " entries; of which " + this.localSize() + " local]";
    }
}
