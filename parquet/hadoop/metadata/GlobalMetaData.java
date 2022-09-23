// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import parquet.Preconditions;
import java.util.Set;
import java.util.Map;
import parquet.schema.MessageType;
import java.io.Serializable;

public class GlobalMetaData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final MessageType schema;
    private final Map<String, Set<String>> keyValueMetaData;
    private final Set<String> createdBy;
    
    public GlobalMetaData(final MessageType schema, final Map<String, Set<String>> keyValueMetaData, final Set<String> createdBy) {
        this.schema = Preconditions.checkNotNull(schema, "schema");
        this.keyValueMetaData = Collections.unmodifiableMap((Map<? extends String, ? extends Set<String>>)Preconditions.checkNotNull((Map<? extends K, ? extends V>)keyValueMetaData, "keyValueMetaData"));
        this.createdBy = createdBy;
    }
    
    public MessageType getSchema() {
        return this.schema;
    }
    
    @Override
    public String toString() {
        return "GlobalMetaData{schema: " + this.schema + ", metadata: " + this.keyValueMetaData + "}";
    }
    
    public Map<String, Set<String>> getKeyValueMetaData() {
        return this.keyValueMetaData;
    }
    
    public Set<String> getCreatedBy() {
        return this.createdBy;
    }
    
    public FileMetaData merge() {
        final String createdByString = (this.createdBy.size() == 1) ? this.createdBy.iterator().next() : this.createdBy.toString();
        final Map<String, String> mergedKeyValues = new HashMap<String, String>();
        for (final Map.Entry<String, Set<String>> entry : this.keyValueMetaData.entrySet()) {
            if (entry.getValue().size() > 1) {
                throw new RuntimeException("could not merge metadata: key " + entry.getKey() + " has conflicting values: " + entry.getValue());
            }
            mergedKeyValues.put(entry.getKey(), entry.getValue().iterator().next());
        }
        return new FileMetaData(this.schema, mergedKeyValues, createdByString);
    }
}
