// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.core;

public interface EnumConstant<T, C>
{
    EnumLeafInfo<T, C> getEnclosingClass();
    
    String getLexicalValue();
    
    String getName();
}
