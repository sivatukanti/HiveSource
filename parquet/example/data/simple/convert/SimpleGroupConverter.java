// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple.convert;

import parquet.schema.Type;
import parquet.schema.GroupType;
import parquet.io.api.Converter;
import parquet.example.data.Group;
import parquet.io.api.GroupConverter;

class SimpleGroupConverter extends GroupConverter
{
    private final SimpleGroupConverter parent;
    private final int index;
    protected Group current;
    private Converter[] converters;
    
    SimpleGroupConverter(final SimpleGroupConverter parent, final int index, final GroupType schema) {
        this.parent = parent;
        this.index = index;
        this.converters = new Converter[schema.getFieldCount()];
        for (int i = 0; i < this.converters.length; ++i) {
            final Type type = schema.getType(i);
            if (type.isPrimitive()) {
                this.converters[i] = new SimplePrimitiveConverter(this, i);
            }
            else {
                this.converters[i] = new SimpleGroupConverter(this, i, type.asGroupType());
            }
        }
    }
    
    @Override
    public void start() {
        this.current = this.parent.getCurrentRecord().addGroup(this.index);
    }
    
    @Override
    public Converter getConverter(final int fieldIndex) {
        return this.converters[fieldIndex];
    }
    
    @Override
    public void end() {
    }
    
    public Group getCurrentRecord() {
        return this.current;
    }
}
