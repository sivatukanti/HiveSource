// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.example;

import parquet.schema.GroupType;
import parquet.io.api.RecordConsumer;
import java.util.Map;
import java.util.HashMap;
import parquet.schema.MessageTypeParser;
import parquet.Preconditions;
import org.apache.hadoop.conf.Configuration;
import parquet.example.data.GroupWriter;
import parquet.schema.MessageType;
import parquet.example.data.Group;
import parquet.hadoop.api.WriteSupport;

public class GroupWriteSupport extends WriteSupport<Group>
{
    public static final String PARQUET_EXAMPLE_SCHEMA = "parquet.example.schema";
    private MessageType schema;
    private GroupWriter groupWriter;
    
    public static void setSchema(final MessageType schema, final Configuration configuration) {
        configuration.set("parquet.example.schema", schema.toString());
    }
    
    public static MessageType getSchema(final Configuration configuration) {
        return MessageTypeParser.parseMessageType(Preconditions.checkNotNull(configuration.get("parquet.example.schema"), "parquet.example.schema"));
    }
    
    @Override
    public WriteContext init(final Configuration configuration) {
        this.schema = getSchema(configuration);
        return new WriteContext(this.schema, new HashMap<String, String>());
    }
    
    @Override
    public void prepareForWrite(final RecordConsumer recordConsumer) {
        this.groupWriter = new GroupWriter(recordConsumer, this.schema);
    }
    
    @Override
    public void write(final Group record) {
        this.groupWriter.write(record);
    }
}
