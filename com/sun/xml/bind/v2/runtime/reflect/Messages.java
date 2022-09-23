// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.reflect;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    UNABLE_TO_ACCESS_NON_PUBLIC_FIELD, 
    UNASSIGNABLE_TYPE, 
    NO_SETTER, 
    NO_GETTER;
    
    private static final ResourceBundle rb;
    
    @Override
    public String toString() {
        return this.format(new Object[0]);
    }
    
    public String format(final Object... args) {
        return MessageFormat.format(Messages.rb.getString(this.name()), args);
    }
    
    static {
        rb = ResourceBundle.getBundle(Messages.class.getName());
    }
}
