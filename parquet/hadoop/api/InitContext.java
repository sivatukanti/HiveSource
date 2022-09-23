// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.api;

import java.util.Iterator;
import java.util.HashMap;
import parquet.schema.MessageType;
import org.apache.hadoop.conf.Configuration;
import java.util.Set;
import java.util.Map;

public class InitContext
{
    private final Map<String, Set<String>> keyValueMetadata;
    private Map<String, String> mergedKeyValueMetadata;
    private final Configuration configuration;
    private final MessageType fileSchema;
    
    public InitContext(final Configuration configuration, final Map<String, Set<String>> keyValueMetadata, final MessageType fileSchema) {
        this.keyValueMetadata = keyValueMetadata;
        this.configuration = configuration;
        this.fileSchema = fileSchema;
    }
    
    @Deprecated
    public Map<String, String> getMergedKeyValueMetaData() {
        if (this.mergedKeyValueMetadata == null) {
            final Map<String, String> mergedKeyValues = new HashMap<String, String>();
            for (final Map.Entry<String, Set<String>> entry : this.keyValueMetadata.entrySet()) {
                if (entry.getValue().size() > 1) {
                    throw new RuntimeException("could not merge metadata: key " + entry.getKey() + " has conflicting values: " + entry.getValue());
                }
                mergedKeyValues.put(entry.getKey(), entry.getValue().iterator().next());
            }
            this.mergedKeyValueMetadata = mergedKeyValues;
        }
        return this.mergedKeyValueMetadata;
    }
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    public MessageType getFileSchema() {
        return this.fileSchema;
    }
    
    public Map<String, Set<String>> getKeyValueMetadata() {
        return this.keyValueMetadata;
    }
}
