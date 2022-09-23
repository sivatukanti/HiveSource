// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.api;

import java.util.Collections;
import parquet.Preconditions;
import parquet.schema.MessageType;
import java.util.Map;
import java.util.HashMap;
import parquet.io.api.RecordConsumer;
import org.apache.hadoop.conf.Configuration;

public abstract class WriteSupport<T>
{
    public abstract WriteContext init(final Configuration p0);
    
    public abstract void prepareForWrite(final RecordConsumer p0);
    
    public abstract void write(final T p0);
    
    public FinalizedWriteContext finalizeWrite() {
        return new FinalizedWriteContext(new HashMap<String, String>());
    }
    
    public static final class WriteContext
    {
        private final MessageType schema;
        private final Map<String, String> extraMetaData;
        
        public WriteContext(final MessageType schema, final Map<String, String> extraMetaData) {
            this.schema = Preconditions.checkNotNull(schema, "schema");
            this.extraMetaData = Collections.unmodifiableMap((Map<? extends String, ? extends String>)Preconditions.checkNotNull((Map<? extends K, ? extends V>)extraMetaData, "extraMetaData"));
        }
        
        public MessageType getSchema() {
            return this.schema;
        }
        
        public Map<String, String> getExtraMetaData() {
            return this.extraMetaData;
        }
    }
    
    public static final class FinalizedWriteContext
    {
        private final Map<String, String> extraMetaData;
        
        public FinalizedWriteContext(final Map<String, String> extraMetaData) {
            this.extraMetaData = Collections.unmodifiableMap((Map<? extends String, ? extends String>)Preconditions.checkNotNull((Map<? extends K, ? extends V>)extraMetaData, "extraMetaData"));
        }
        
        public Map<String, String> getExtraMetaData() {
            return this.extraMetaData;
        }
    }
}
