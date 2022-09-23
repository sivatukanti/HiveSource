// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter;

import parquet.column.ColumnReader;
import parquet.Preconditions;

public final class NotRecordFilter implements RecordFilter
{
    private final RecordFilter boundFilter;
    
    public static final UnboundRecordFilter not(final UnboundRecordFilter filter) {
        Preconditions.checkNotNull(filter, "filter");
        return new UnboundRecordFilter() {
            @Override
            public RecordFilter bind(final Iterable<ColumnReader> readers) {
                return new NotRecordFilter(filter.bind(readers), null);
            }
        };
    }
    
    private NotRecordFilter(final RecordFilter boundFilter) {
        this.boundFilter = boundFilter;
    }
    
    @Override
    public boolean isMatch() {
        return !this.boundFilter.isMatch();
    }
}
