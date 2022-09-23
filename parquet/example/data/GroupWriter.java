// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data;

import parquet.schema.Type;
import parquet.schema.GroupType;
import parquet.io.api.RecordConsumer;

public class GroupWriter
{
    private final RecordConsumer recordConsumer;
    private final GroupType schema;
    
    public GroupWriter(final RecordConsumer recordConsumer, final GroupType schema) {
        this.recordConsumer = recordConsumer;
        this.schema = schema;
    }
    
    public void write(final Group group) {
        this.recordConsumer.startMessage();
        this.writeGroup(group, this.schema);
        this.recordConsumer.endMessage();
    }
    
    private void writeGroup(final Group group, final GroupType type) {
        for (int fieldCount = type.getFieldCount(), field = 0; field < fieldCount; ++field) {
            final int valueCount = group.getFieldRepetitionCount(field);
            if (valueCount > 0) {
                final Type fieldType = type.getType(field);
                final String fieldName = fieldType.getName();
                this.recordConsumer.startField(fieldName, field);
                for (int index = 0; index < valueCount; ++index) {
                    if (fieldType.isPrimitive()) {
                        group.writeValue(field, index, this.recordConsumer);
                    }
                    else {
                        this.recordConsumer.startGroup();
                        this.writeGroup(group.getGroup(field, index), fieldType.asGroupType());
                        this.recordConsumer.endGroup();
                    }
                }
                this.recordConsumer.endField(fieldName, field);
            }
        }
    }
}
