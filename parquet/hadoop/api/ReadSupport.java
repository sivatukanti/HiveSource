// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.api;

import parquet.io.api.RecordMaterializer;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import parquet.schema.Type;
import parquet.schema.MessageTypeParser;
import parquet.schema.MessageType;

public abstract class ReadSupport<T>
{
    public static final String PARQUET_READ_SCHEMA = "parquet.read.schema";
    
    public static MessageType getSchemaForRead(final MessageType fileMessageType, final String partialReadSchemaString) {
        if (partialReadSchemaString == null) {
            return fileMessageType;
        }
        final MessageType requestedMessageType = MessageTypeParser.parseMessageType(partialReadSchemaString);
        return getSchemaForRead(fileMessageType, requestedMessageType);
    }
    
    public static MessageType getSchemaForRead(final MessageType fileMessageType, final MessageType projectedMessageType) {
        fileMessageType.checkContains(projectedMessageType);
        return projectedMessageType;
    }
    
    @Deprecated
    public ReadContext init(final Configuration configuration, final Map<String, String> keyValueMetaData, final MessageType fileSchema) {
        throw new UnsupportedOperationException("Override init(InitContext)");
    }
    
    public ReadContext init(final InitContext context) {
        return this.init(context.getConfiguration(), context.getMergedKeyValueMetaData(), context.getFileSchema());
    }
    
    public abstract RecordMaterializer<T> prepareForRead(final Configuration p0, final Map<String, String> p1, final MessageType p2, final ReadContext p3);
    
    public static final class ReadContext
    {
        private final MessageType requestedSchema;
        private final Map<String, String> readSupportMetadata;
        
        public ReadContext(final MessageType requestedSchema) {
            this(requestedSchema, null);
        }
        
        public ReadContext(final MessageType requestedSchema, final Map<String, String> readSupportMetadata) {
            if (requestedSchema == null) {
                throw new NullPointerException("requestedSchema");
            }
            this.requestedSchema = requestedSchema;
            this.readSupportMetadata = readSupportMetadata;
        }
        
        public MessageType getRequestedSchema() {
            return this.requestedSchema;
        }
        
        public Map<String, String> getReadSupportMetadata() {
            return this.readSupportMetadata;
        }
    }
}
