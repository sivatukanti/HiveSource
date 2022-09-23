// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.api;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    ARGUMENT_CANT_BE_NULL;
    
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
