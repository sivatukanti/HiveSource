// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

import java.util.HashMap;
import java.util.Map;

public class Message
{
    private static Map<MessageCode, String> entries;
    
    public static void init() {
    }
    
    public static void define(final MessageCode code, final String message) {
        Message.entries.put(code, message);
    }
    
    public static String getMessage(final MessageCode code) {
        String msg = Message.entries.get(code);
        if (msg == null) {
            msg = code.getCodeName();
        }
        return msg;
    }
    
    static {
        Message.entries = new HashMap<MessageCode, String>();
    }
}
