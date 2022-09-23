// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple.convert;

import parquet.io.api.GroupConverter;
import parquet.schema.GroupType;
import parquet.schema.MessageType;
import parquet.example.data.simple.SimpleGroupFactory;
import parquet.example.data.Group;
import parquet.io.api.RecordMaterializer;

public class GroupRecordConverter extends RecordMaterializer<Group>
{
    private final SimpleGroupFactory simpleGroupFactory;
    private SimpleGroupConverter root;
    
    public GroupRecordConverter(final MessageType schema) {
        this.simpleGroupFactory = new SimpleGroupFactory(schema);
        this.root = new SimpleGroupConverter(null, 0, schema) {
            @Override
            public void start() {
                this.current = GroupRecordConverter.this.simpleGroupFactory.newGroup();
            }
            
            @Override
            public void end() {
            }
        };
    }
    
    @Override
    public Group getCurrentRecord() {
        return this.root.getCurrentRecord();
    }
    
    @Override
    public GroupConverter getRootConverter() {
        return this.root;
    }
}
