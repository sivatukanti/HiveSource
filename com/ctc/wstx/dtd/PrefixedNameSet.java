// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

public abstract class PrefixedNameSet
{
    protected PrefixedNameSet() {
    }
    
    public abstract boolean hasMultiple();
    
    public abstract boolean contains(final PrefixedName p0);
    
    public abstract void appendNames(final StringBuilder p0, final String p1);
    
    @Override
    public final String toString() {
        return this.toString(", ");
    }
    
    public final String toString(final String sep) {
        final StringBuilder sb = new StringBuilder();
        this.appendNames(sb, sep);
        return sb.toString();
    }
}
