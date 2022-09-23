// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.Collections;
import parquet.Preconditions;
import java.util.Map;
import parquet.schema.MessageType;
import java.io.Serializable;

public final class FileMetaData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final MessageType schema;
    private final Map<String, String> keyValueMetaData;
    private final String createdBy;
    
    public FileMetaData(final MessageType schema, final Map<String, String> keyValueMetaData, final String createdBy) {
        this.schema = Preconditions.checkNotNull(schema, "schema");
        this.keyValueMetaData = Collections.unmodifiableMap((Map<? extends String, ? extends String>)Preconditions.checkNotNull((Map<? extends K, ? extends V>)keyValueMetaData, "keyValueMetaData"));
        this.createdBy = createdBy;
    }
    
    public MessageType getSchema() {
        return this.schema;
    }
    
    @Override
    public String toString() {
        return "FileMetaData{schema: " + this.schema + ", metadata: " + this.keyValueMetaData + "}";
    }
    
    public Map<String, String> getKeyValueMetaData() {
        return this.keyValueMetaData;
    }
    
    public String getCreatedBy() {
        return this.createdBy;
    }
}
