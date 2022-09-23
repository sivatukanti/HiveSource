// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.ParquetRuntimeException;

public class StatisticsClassException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public StatisticsClassException(final String className1, final String className2) {
        super("Statistics classes mismatched: " + className1 + " vs. " + className2);
    }
}
