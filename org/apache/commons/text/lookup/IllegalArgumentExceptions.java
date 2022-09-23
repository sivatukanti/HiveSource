// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.lookup;

final class IllegalArgumentExceptions
{
    static IllegalArgumentException format(final String format, final Object... args) {
        return new IllegalArgumentException(String.format(format, args));
    }
    
    static IllegalArgumentException format(final Throwable t, final String format, final Object... args) {
        return new IllegalArgumentException(String.format(format, args), t);
    }
    
    private IllegalArgumentExceptions() {
    }
}
