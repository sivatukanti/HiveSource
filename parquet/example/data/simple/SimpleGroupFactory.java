// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.schema.GroupType;
import parquet.example.data.Group;
import parquet.schema.MessageType;
import parquet.example.data.GroupFactory;

public class SimpleGroupFactory extends GroupFactory
{
    private final MessageType schema;
    
    public SimpleGroupFactory(final MessageType schema) {
        this.schema = schema;
    }
    
    @Override
    public Group newGroup() {
        return new SimpleGroup(this.schema);
    }
}
