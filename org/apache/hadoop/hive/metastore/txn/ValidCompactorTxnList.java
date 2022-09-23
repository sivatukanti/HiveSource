// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.txn;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.hive.common.ValidTxnList;
import org.apache.hadoop.hive.common.ValidReadTxnList;

public class ValidCompactorTxnList extends ValidReadTxnList
{
    private long minOpenTxn;
    
    public ValidCompactorTxnList() {
        this.minOpenTxn = -1L;
    }
    
    public ValidCompactorTxnList(final long[] exceptions, final long minOpen, final long highWatermark) {
        super(exceptions, highWatermark);
        this.minOpenTxn = minOpen;
    }
    
    public ValidCompactorTxnList(final String value) {
        super(value);
    }
    
    @Override
    public ValidTxnList.RangeResponse isTxnRangeValid(final long minTxnId, final long maxTxnId) {
        if (this.highWatermark < minTxnId) {
            return ValidTxnList.RangeResponse.NONE;
        }
        if (this.minOpenTxn < 0L) {
            return (this.highWatermark >= maxTxnId) ? ValidTxnList.RangeResponse.ALL : ValidTxnList.RangeResponse.NONE;
        }
        return (this.minOpenTxn > maxTxnId) ? ValidTxnList.RangeResponse.ALL : ValidTxnList.RangeResponse.NONE;
    }
    
    @Override
    public String writeToString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.highWatermark);
        buf.append(':');
        buf.append(this.minOpenTxn);
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
            this.minOpenTxn = Long.parseLong(values[1]);
            this.exceptions = new long[values.length - 2];
            for (int i = 2; i < values.length; ++i) {
                this.exceptions[i - 2] = Long.parseLong(values[i]);
            }
        }
    }
    
    @VisibleForTesting
    long getMinOpenTxn() {
        return this.minOpenTxn;
    }
}
