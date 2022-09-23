// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.column.ColumnReader;
import parquet.filter.UnboundRecordFilter;
import parquet.column.impl.ColumnReadStoreImpl;
import parquet.io.api.RecordMaterializer;
import parquet.filter.RecordFilter;

class FilteredRecordReader<T> extends RecordReaderImplementation<T>
{
    private final RecordFilter recordFilter;
    private final long recordCount;
    private long recordsRead;
    
    public FilteredRecordReader(final MessageColumnIO root, final RecordMaterializer<T> recordMaterializer, final boolean validating, final ColumnReadStoreImpl columnStore, final UnboundRecordFilter unboundFilter, final long recordCount) {
        super(root, recordMaterializer, validating, columnStore);
        this.recordsRead = 0L;
        this.recordCount = recordCount;
        if (unboundFilter != null) {
            this.recordFilter = unboundFilter.bind(this.getColumnReaders());
        }
        else {
            this.recordFilter = null;
        }
    }
    
    @Override
    public T read() {
        this.skipToMatch();
        if (this.recordsRead == this.recordCount) {
            return null;
        }
        ++this.recordsRead;
        return super.read();
    }
    
    @Override
    public boolean shouldSkipCurrentRecord() {
        return false;
    }
    
    private void skipToMatch() {
        while (this.recordsRead < this.recordCount && !this.recordFilter.isMatch()) {
            State currentState = this.getState(0);
            do {
                final ColumnReader columnReader = currentState.column;
                if (columnReader.getCurrentDefinitionLevel() >= currentState.maxDefinitionLevel) {
                    columnReader.skip();
                }
                columnReader.consume();
                final int nextR = (currentState.maxRepetitionLevel == 0) ? 0 : columnReader.getCurrentRepetitionLevel();
                currentState = currentState.getNextState(nextR);
            } while (currentState != null);
            ++this.recordsRead;
        }
    }
}
