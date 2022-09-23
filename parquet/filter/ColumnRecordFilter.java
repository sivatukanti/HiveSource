// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter;

import java.util.Iterator;
import java.util.Arrays;
import parquet.Preconditions;
import parquet.column.ColumnReader;

public final class ColumnRecordFilter implements RecordFilter
{
    private final ColumnReader filterOnColumn;
    private final ColumnPredicates.Predicate filterPredicate;
    
    public static final UnboundRecordFilter column(final String columnPath, final ColumnPredicates.Predicate predicate) {
        Preconditions.checkNotNull(columnPath, "columnPath");
        Preconditions.checkNotNull(predicate, "predicate");
        return new UnboundRecordFilter() {
            final String[] filterPath = columnPath.split("\\.");
            
            @Override
            public RecordFilter bind(final Iterable<ColumnReader> readers) {
                for (final ColumnReader reader : readers) {
                    if (Arrays.equals(reader.getDescriptor().getPath(), this.filterPath)) {
                        return new ColumnRecordFilter(reader, predicate, null);
                    }
                }
                throw new IllegalArgumentException("Column " + columnPath + " does not exist.");
            }
        };
    }
    
    private ColumnRecordFilter(final ColumnReader filterOnColumn, final ColumnPredicates.Predicate filterPredicate) {
        this.filterOnColumn = filterOnColumn;
        this.filterPredicate = filterPredicate;
    }
    
    @Override
    public boolean isMatch() {
        return this.filterPredicate.apply(this.filterOnColumn);
    }
}
