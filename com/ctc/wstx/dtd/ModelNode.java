// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

public abstract class ModelNode
{
    public abstract ModelNode cloneModel();
    
    public abstract boolean isNullable();
    
    public abstract void indexTokens(final List<TokenModel> p0);
    
    public abstract void addFirstPos(final BitSet p0);
    
    public abstract void addLastPos(final BitSet p0);
    
    public abstract void calcFollowPos(final BitSet[] p0);
}
