// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

public abstract class StructValidator
{
    public abstract StructValidator newInstance();
    
    public abstract String tryToValidate(final PrefixedName p0);
    
    public abstract String fullyValid();
}
