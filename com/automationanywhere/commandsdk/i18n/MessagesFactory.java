// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.commandsdk.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class MessagesFactory
{
    private static final Map<String, Messages> cache;
    
    private MessagesFactory() {
    }
    
    public static Messages getMessages(final String bundleName) {
        final String locale = Locale.getDefault().toString();
        final String key = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, bundleName, locale);
        if (MessagesFactory.cache.containsKey(key)) {
            return MessagesFactory.cache.get(key);
        }
        synchronized (MessagesFactory.class) {
            if (MessagesFactory.cache.containsKey(key)) {
                return MessagesFactory.cache.get(key);
            }
            final Messages msg = new Messages(bundleName);
            MessagesFactory.cache.put(key, msg);
            return msg;
        }
    }
    
    static {
        cache = new HashMap<String, Messages>();
    }
}
