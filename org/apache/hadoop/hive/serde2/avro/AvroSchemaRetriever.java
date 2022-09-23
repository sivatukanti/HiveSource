// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.avro.Schema;

public abstract class AvroSchemaRetriever
{
    public abstract Schema retrieveWriterSchema(final Object p0);
    
    public Schema retrieveReaderSchema(final Object source) {
        return null;
    }
    
    public int getOffset() {
        return 0;
    }
}
