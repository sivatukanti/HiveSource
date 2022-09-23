// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class Contracts
{
    private Contracts() {
    }
    
    public static <T> T checkArg(final T arg, final boolean expression, final Object msg) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(msg) + ": " + arg);
        }
        return arg;
    }
    
    public static int checkArg(final int arg, final boolean expression, final Object msg) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(msg) + ": " + arg);
        }
        return arg;
    }
    
    public static long checkArg(final long arg, final boolean expression, final Object msg) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(msg) + ": " + arg);
        }
        return arg;
    }
    
    public static float checkArg(final float arg, final boolean expression, final Object msg) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(msg) + ": " + arg);
        }
        return arg;
    }
    
    public static double checkArg(final double arg, final boolean expression, final Object msg) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(msg) + ": " + arg);
        }
        return arg;
    }
}
