// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.example;

import parquet.example.data.simple.convert.GroupRecordConverter;
import parquet.io.api.RecordMaterializer;
import parquet.schema.MessageType;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import parquet.example.data.Group;
import parquet.hadoop.api.ReadSupport;

public class GroupReadSupport extends ReadSupport<Group>
{
    @Override
    public ReadContext init(final Configuration configuration, final Map<String, String> keyValueMetaData, final MessageType fileSchema) {
        final String partialSchemaString = configuration.get("parquet.read.schema");
        final MessageType requestedProjection = ReadSupport.getSchemaForRead(fileSchema, partialSchemaString);
        return new ReadContext(requestedProjection);
    }
    
    @Override
    public RecordMaterializer<Group> prepareForRead(final Configuration configuration, final Map<String, String> keyValueMetaData, final MessageType fileSchema, final ReadContext readContext) {
        return new GroupRecordConverter(readContext.getRequestedSchema());
    }
}
