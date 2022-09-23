// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.io.api.RecordMaterializer;
import parquet.io.api.GroupConverter;

class EmptyRecordReader<T> extends RecordReader<T>
{
    private final GroupConverter recordConsumer;
    private final RecordMaterializer<T> recordMaterializer;
    
    public EmptyRecordReader(final RecordMaterializer<T> recordMaterializer) {
        this.recordMaterializer = recordMaterializer;
        this.recordConsumer = recordMaterializer.getRootConverter();
    }
    
    @Override
    public T read() {
        this.recordConsumer.start();
        this.recordConsumer.end();
        return this.recordMaterializer.getCurrentRecord();
    }
}
