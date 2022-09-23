// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.catalog.SequencePreallocator;

public class SequenceRange implements SequencePreallocator
{
    private static final int DEFAULT_PREALLOCATION_COUNT = 100;
    private int _rangeSize;
    
    public SequenceRange() {
        this(100);
    }
    
    public SequenceRange(int rangeSize) {
        if (rangeSize <= 0) {
            rangeSize = 100;
        }
        this._rangeSize = rangeSize;
    }
    
    public int nextRangeSize(final String s, final String s2) {
        return this._rangeSize;
    }
}
