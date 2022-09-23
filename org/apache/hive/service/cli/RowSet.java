// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TRowSet;

public interface RowSet extends Iterable<Object[]>
{
    RowSet addRow(final Object[] p0);
    
    RowSet extractSubset(final int p0);
    
    int numColumns();
    
    int numRows();
    
    long getStartOffset();
    
    void setStartOffset(final long p0);
    
    TRowSet toTRowSet();
}
