// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.SequencePreallocator;

public class SequenceGenerator
{
    private static final int PREALLOCATION_THRESHHOLD = 1;
    public static final int RET_I_AM_CONFUSED = 0;
    public static final int RET_OK = 1;
    public static final int RET_MARK_EXHAUSTED = 2;
    public static final int RET_ALLOCATE_NEW_VALUES = 3;
    public static final int CVAA_STATUS = 0;
    public static final int CVAA_CURRENT_VALUE = 1;
    public static final int CVAA_LAST_ALLOCATED_VALUE = 2;
    public static final int CVAA_NUMBER_OF_VALUES_ALLOCATED = 3;
    public static final int CVAA_LENGTH = 4;
    private final boolean _CAN_CYCLE;
    private final boolean _STEP_INCREASES;
    private final long _INCREMENT;
    private final long _MAX_VALUE;
    private final long _MIN_VALUE;
    private final long _RESTART_VALUE;
    private final String _SCHEMA_NAME;
    private final String _SEQUENCE_NAME;
    private final SequencePreallocator _PREALLOCATOR;
    private boolean _isExhausted;
    private long _currentValue;
    private long _remainingPreallocatedValues;
    
    public SequenceGenerator(final Long n, final boolean can_CYCLE, final long increment, final long max_VALUE, final long min_VALUE, final long restart_VALUE, final String schema_NAME, final String sequence_NAME, final SequencePreallocator preallocator) {
        if (n == null) {
            this._isExhausted = true;
            this._currentValue = 0L;
        }
        else {
            this._isExhausted = false;
            this._currentValue = n;
        }
        this._CAN_CYCLE = can_CYCLE;
        this._INCREMENT = increment;
        this._MAX_VALUE = max_VALUE;
        this._MIN_VALUE = min_VALUE;
        this._RESTART_VALUE = restart_VALUE;
        this._STEP_INCREASES = (this._INCREMENT > 0L);
        this._SCHEMA_NAME = schema_NAME;
        this._SEQUENCE_NAME = sequence_NAME;
        this._PREALLOCATOR = preallocator;
        this._remainingPreallocatedValues = 1L;
    }
    
    public synchronized String getSchemaName() {
        return this._SCHEMA_NAME;
    }
    
    public synchronized String getName() {
        return this._SEQUENCE_NAME;
    }
    
    public synchronized void allocateNewRange(final long n, final long remainingPreallocatedValues) {
        if (this._currentValue == n) {
            this._remainingPreallocatedValues = remainingPreallocatedValues;
        }
    }
    
    public synchronized Long peekAtCurrentValue() {
        Long n = null;
        if (!this._isExhausted) {
            n = new Long(this._currentValue);
        }
        return n;
    }
    
    public synchronized long[] getCurrentValueAndAdvance() throws StandardException {
        if (this._isExhausted) {
            throw StandardException.newException("2200H.S", this._SCHEMA_NAME, this._SEQUENCE_NAME);
        }
        final long[] array = { 0L, this._currentValue, 0L, 0L };
        this.advanceValue(array);
        return array;
    }
    
    private void advanceValue(final long[] array) throws StandardException {
        long restart_VALUE = this._currentValue + this._INCREMENT;
        if (this.overflowed(this._currentValue, restart_VALUE)) {
            if (!this._CAN_CYCLE) {
                this.markExhausted(array);
                return;
            }
            restart_VALUE = this._RESTART_VALUE;
        }
        --this._remainingPreallocatedValues;
        if (this._remainingPreallocatedValues < 1L) {
            this.computeNewAllocation(this._currentValue, array);
            return;
        }
        this._currentValue = restart_VALUE;
        array[0] = 1L;
    }
    
    private void markExhausted(final long[] array) {
        this._isExhausted = true;
        array[0] = 2L;
    }
    
    private boolean overflowed(final long n, final long n2) {
        boolean b = this._STEP_INCREASES == n2 < n;
        if (!b) {
            if (this._STEP_INCREASES) {
                b = (n2 > this._MAX_VALUE);
            }
            else {
                b = (n2 < this._MIN_VALUE);
            }
        }
        return b;
    }
    
    private void computeNewAllocation(final long n, final long[] array) throws StandardException {
        final int computePreAllocationCount = this.computePreAllocationCount();
        final long computeRemainingValues = this.computeRemainingValues(n);
        long n2;
        long n3;
        if (computeRemainingValues >= computePreAllocationCount) {
            n2 = n + computePreAllocationCount * this._INCREMENT;
            n3 = computePreAllocationCount;
        }
        else if (this._CAN_CYCLE) {
            n2 = this._RESTART_VALUE + (computePreAllocationCount - computeRemainingValues - 1L) * this._INCREMENT;
            n3 = computePreAllocationCount;
        }
        else {
            if (computeRemainingValues <= 0L) {
                this.markExhausted(array);
                return;
            }
            n3 = computeRemainingValues;
            n2 = n + n3 * this._INCREMENT;
        }
        array[3] = n3 + 1L;
        array[2] = n2;
        array[0] = 3L;
    }
    
    private long computeRemainingValues(final long n) {
        long n2 = this._STEP_INCREASES ? (this._MAX_VALUE - n) : (-(this._MIN_VALUE - n));
        if (n2 < 0L) {
            n2 = Long.MAX_VALUE;
        }
        return n2 / (this._STEP_INCREASES ? this._INCREMENT : (-this._INCREMENT));
    }
    
    private int computePreAllocationCount() {
        final int nextRangeSize = this._PREALLOCATOR.nextRangeSize(this._SCHEMA_NAME, this._SEQUENCE_NAME);
        final int n = 1;
        if (nextRangeSize < n) {
            return n;
        }
        final double n2 = this._MAX_VALUE - (double)this._MIN_VALUE;
        double n3 = (double)this._INCREMENT;
        if (n3 < 0.0) {
            n3 = -n3;
        }
        final double n4 = n3 * nextRangeSize;
        if (n4 > 9.223372036854776E18) {
            return n;
        }
        if (n4 > n2) {
            return n;
        }
        return nextRangeSize;
    }
}
