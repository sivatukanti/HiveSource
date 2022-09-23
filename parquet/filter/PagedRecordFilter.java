// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter;

import parquet.column.ColumnReader;

public final class PagedRecordFilter implements RecordFilter
{
    private final long startPos;
    private final long endPos;
    private long currentPos;
    
    public static final UnboundRecordFilter page(final long startPos, final long pageSize) {
        return new UnboundRecordFilter() {
            @Override
            public RecordFilter bind(final Iterable<ColumnReader> readers) {
                return new PagedRecordFilter(startPos, pageSize, null);
            }
        };
    }
    
    private PagedRecordFilter(final long startPos, final long pageSize) {
        this.currentPos = 0L;
        this.startPos = startPos;
        this.endPos = startPos + pageSize;
    }
    
    @Override
    public boolean isMatch() {
        ++this.currentPos;
        return this.currentPos >= this.startPos && this.currentPos < this.endPos;
    }
}
