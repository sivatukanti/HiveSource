// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import java.util.Arrays;

public class ValidReadTxnList implements ValidTxnList
{
    protected long[] exceptions;
    protected long highWatermark;
    
    public ValidReadTxnList() {
        this(new long[0], Long.MAX_VALUE);
    }
    
    public ValidReadTxnList(final long[] exceptions, final long highWatermark) {
        if (exceptions.length == 0) {
            this.exceptions = exceptions;
        }
        else {
            Arrays.sort(this.exceptions = exceptions.clone());
        }
        this.highWatermark = highWatermark;
    }
    
    public ValidReadTxnList(final String value) {
        this.readFromString(value);
    }
    
    @Override
    public boolean isTxnValid(final long txnid) {
        return this.highWatermark >= txnid && Arrays.binarySearch(this.exceptions, txnid) < 0;
    }
    
    @Override
    public RangeResponse isTxnRangeValid(final long minTxnId, final long maxTxnId) {
        if (this.highWatermark < minTxnId) {
            return RangeResponse.NONE;
        }
        if (this.exceptions.length > 0 && this.exceptions[0] > maxTxnId) {
            return RangeResponse.ALL;
        }
        long count = Math.max(0L, maxTxnId - this.highWatermark);
        for (final long txn : this.exceptions) {
            if (minTxnId <= txn && txn <= maxTxnId) {
                ++count;
            }
        }
        if (count == 0L) {
            return RangeResponse.ALL;
        }
        if (count == maxTxnId - minTxnId + 1L) {
            return RangeResponse.NONE;
        }
        return RangeResponse.SOME;
    }
    
    @Override
    public String toString() {
        return this.writeToString();
    }
    
    @Override
    public String writeToString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.highWatermark);
        if (this.exceptions.length == 0) {
            buf.append(':');
        }
        else {
            for (final long except : this.exceptions) {
                buf.append(':');
                buf.append(except);
            }
        }
        return buf.toString();
    }
    
    @Override
    public void readFromString(final String src) {
        if (src == null) {
            this.highWatermark = Long.MAX_VALUE;
            this.exceptions = new long[0];
        }
        else {
            final String[] values = src.split(":");
            this.highWatermark = Long.parseLong(values[0]);
            this.exceptions = new long[values.length - 1];
            for (int i = 1; i < values.length; ++i) {
                this.exceptions[i - 1] = Long.parseLong(values[i]);
            }
        }
    }
    
    @Override
    public long getHighWatermark() {
        return this.highWatermark;
    }
    
    @Override
    public long[] getInvalidTransactions() {
        return this.exceptions;
    }
}
