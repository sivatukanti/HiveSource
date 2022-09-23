// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.commandsdk.i18n;

import java.util.IllegalFormatException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
    private final ResourceBundle RESOURCE_BUNDLE;
    
    Messages(final String bundleName) {
        this.RESOURCE_BUNDLE = ResourceBundle.getBundle(bundleName, new UTF8Control());
    }
    
    public String getString(final String key, final Object... objects) {
        try {
            return String.format(this.RESOURCE_BUNDLE.getString(key), objects);
        }
        catch (MissingResourceException | IllegalFormatException ex2) {
            final RuntimeException ex;
            final RuntimeException e = ex;
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key);
        }
    }
}
