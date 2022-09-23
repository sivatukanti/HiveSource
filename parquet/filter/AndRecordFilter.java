// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter;

import parquet.column.ColumnReader;
import parquet.Preconditions;

public final class AndRecordFilter implements RecordFilter
{
    private final RecordFilter boundFilter1;
    private final RecordFilter boundFilter2;
    
    public static final UnboundRecordFilter and(final UnboundRecordFilter filter1, final UnboundRecordFilter filter2) {
        Preconditions.checkNotNull(filter1, "filter1");
        Preconditions.checkNotNull(filter2, "filter2");
        return new UnboundRecordFilter() {
            @Override
            public RecordFilter bind(final Iterable<ColumnReader> readers) {
                return new AndRecordFilter(filter1.bind(readers), filter2.bind(readers), null);
            }
        };
    }
    
    private AndRecordFilter(final RecordFilter boundFilter1, final RecordFilter boundFilter2) {
        this.boundFilter1 = boundFilter1;
        this.boundFilter2 = boundFilter2;
    }
    
    @Override
    public boolean isMatch() {
        return this.boundFilter1.isMatch() && this.boundFilter2.isMatch();
    }
}
