// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.rangecoder;

import java.util.Arrays;

public abstract class RangeCoder
{
    static final int SHIFT_BITS = 8;
    static final int TOP_MASK = -16777216;
    static final int BIT_MODEL_TOTAL_BITS = 11;
    static final int BIT_MODEL_TOTAL = 2048;
    static final short PROB_INIT = 1024;
    static final int MOVE_BITS = 5;
    
    public static final void initProbs(final short[] a) {
        Arrays.fill(a, (short)1024);
    }
}
