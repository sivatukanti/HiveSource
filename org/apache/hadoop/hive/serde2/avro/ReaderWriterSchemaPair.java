// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.avro.Schema;

class ReaderWriterSchemaPair
{
    final Schema reader;
    final Schema writer;
    
    public ReaderWriterSchemaPair(final Schema writer, final Schema reader) {
        this.reader = reader;
        this.writer = writer;
    }
    
    public Schema getReader() {
        return this.reader;
    }
    
    public Schema getWriter() {
        return this.writer;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ReaderWriterSchemaPair that = (ReaderWriterSchemaPair)o;
        return this.reader.equals(that.reader) && this.writer.equals(that.writer);
    }
    
    @Override
    public int hashCode() {
        int result = this.reader.hashCode();
        result = 31 * result + this.writer.hashCode();
        return result;
    }
}
