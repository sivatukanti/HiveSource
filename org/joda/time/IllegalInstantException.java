// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.format.DateTimeFormat;

public class IllegalInstantException extends IllegalArgumentException
{
    private static final long serialVersionUID = 2858712538216L;
    
    public IllegalInstantException(final String s) {
        super(s);
    }
    
    public IllegalInstantException(final long n, final String s) {
        super(createMessage(n, s));
    }
    
    private static String createMessage(final long n, final String str) {
        return "Illegal instant due to time zone offset transition (daylight savings time 'gap'): " + DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").print(new Instant(n)) + ((str != null) ? (" (" + str + ")") : "");
    }
    
    public static boolean isIllegalInstant(final Throwable t) {
        return t instanceof IllegalInstantException || (t.getCause() != null && t.getCause() != t && isIllegalInstant(t.getCause()));
    }
}
