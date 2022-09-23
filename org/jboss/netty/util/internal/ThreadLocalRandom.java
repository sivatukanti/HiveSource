// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.Random;

public final class ThreadLocalRandom extends Random
{
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 281474976710655L;
    private long rnd;
    private boolean initialized;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private static final ThreadLocal<ThreadLocalRandom> localRandom;
    private static final long serialVersionUID = -5851777807851030925L;
    
    public static ThreadLocalRandom current() {
        return ThreadLocalRandom.localRandom.get();
    }
    
    @Override
    public void setSeed(final long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
        this.initialized = true;
        this.rnd = ((seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL);
    }
    
    @Override
    protected int next(final int bits) {
        this.rnd = (this.rnd * 25214903917L + 11L & 0xFFFFFFFFFFFFL);
        return (int)(this.rnd >>> 48 - bits);
    }
    
    static {
        localRandom = new ThreadLocal<ThreadLocalRandom>() {
            @Override
            protected ThreadLocalRandom initialValue() {
                return new ThreadLocalRandom();
            }
        };
    }
}
