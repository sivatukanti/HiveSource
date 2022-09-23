// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.store.raw.log.LogInstant;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.store.raw.log.LogScan;

public interface StreamLogScan extends LogScan
{
    LogRecord getNextRecord(final ArrayInputStream p0, final TransactionId p1, final int p2) throws StandardException, IOException;
    
    long getInstant();
    
    long getLogRecordEnd();
    
    boolean isLogEndFuzzy();
    
    LogInstant getLogInstant();
    
    void resetPosition(final LogInstant p0) throws IOException, StandardException;
    
    void close();
}
