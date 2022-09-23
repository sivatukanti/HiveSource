// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

public interface ValidTxnList
{
    public static final String VALID_TXNS_KEY = "hive.txn.valid.txns";
    
    boolean isTxnValid(final long p0);
    
    RangeResponse isTxnRangeValid(final long p0, final long p1);
    
    String writeToString();
    
    void readFromString(final String p0);
    
    long getHighWatermark();
    
    long[] getInvalidTransactions();
    
    public enum RangeResponse
    {
        NONE, 
        SOME, 
        ALL;
    }
}
