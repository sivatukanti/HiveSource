// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

public class ThrowHelper
{
    public static <T extends Exception> T withInitCause(final Exception cause, final T effect) {
        effect.initCause(cause);
        return effect;
    }
}
