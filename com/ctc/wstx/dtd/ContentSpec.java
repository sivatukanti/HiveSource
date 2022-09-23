// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

public abstract class ContentSpec
{
    protected char mArity;
    
    public ContentSpec(final char arity) {
        this.mArity = arity;
    }
    
    public final char getArity() {
        return this.mArity;
    }
    
    public final void setArity(final char c) {
        this.mArity = c;
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public abstract StructValidator getSimpleValidator();
    
    public abstract ModelNode rewrite();
}
