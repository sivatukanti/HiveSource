// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

public final class SmallPrefixedNameSet extends PrefixedNameSet
{
    final boolean mNsAware;
    final String[] mStrings;
    
    public SmallPrefixedNameSet(final boolean nsAware, final PrefixedName[] names) {
        this.mNsAware = nsAware;
        final int len = names.length;
        if (len == 0) {
            throw new IllegalStateException("Trying to construct empty PrefixedNameSet");
        }
        this.mStrings = new String[nsAware ? (len + len) : len];
        int out = 0;
        for (final PrefixedName nk : names) {
            if (nsAware) {
                this.mStrings[out++] = nk.getPrefix();
            }
            this.mStrings[out++] = nk.getLocalName();
        }
    }
    
    @Override
    public boolean hasMultiple() {
        return this.mStrings.length > 1;
    }
    
    @Override
    public boolean contains(final PrefixedName name) {
        final int len = this.mStrings.length;
        final String ln = name.getLocalName();
        final String[] strs = this.mStrings;
        if (this.mNsAware) {
            final String prefix = name.getPrefix();
            if (strs[1] == ln && strs[0] == prefix) {
                return true;
            }
            for (int i = 2; i < len; i += 2) {
                if (strs[i + 1] == ln && strs[i] == prefix) {
                    return true;
                }
            }
        }
        else {
            if (strs[0] == ln) {
                return true;
            }
            for (int j = 1; j < len; ++j) {
                if (strs[j] == ln) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void appendNames(final StringBuilder sb, final String sep) {
        int i = 0;
        while (i < this.mStrings.length) {
            if (i > 0) {
                sb.append(sep);
            }
            if (this.mNsAware) {
                final String prefix = this.mStrings[i++];
                if (prefix != null) {
                    sb.append(prefix);
                    sb.append(':');
                }
            }
            sb.append(this.mStrings[i++]);
        }
    }
}
