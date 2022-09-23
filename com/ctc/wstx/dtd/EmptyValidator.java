// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

public class EmptyValidator extends StructValidator
{
    static final EmptyValidator sPcdataInstance;
    static final EmptyValidator sEmptyInstance;
    final String mErrorMsg;
    
    private EmptyValidator(final String errorMsg) {
        this.mErrorMsg = errorMsg;
    }
    
    public static EmptyValidator getPcdataInstance() {
        return EmptyValidator.sPcdataInstance;
    }
    
    public static EmptyValidator getEmptyInstance() {
        return EmptyValidator.sPcdataInstance;
    }
    
    @Override
    public StructValidator newInstance() {
        return this;
    }
    
    @Override
    public String tryToValidate(final PrefixedName elemName) {
        return this.mErrorMsg;
    }
    
    @Override
    public String fullyValid() {
        return null;
    }
    
    static {
        sPcdataInstance = new EmptyValidator("No elements allowed in pure #PCDATA content model");
        sEmptyInstance = new EmptyValidator("No elements allowed in EMPTY content model");
    }
}
