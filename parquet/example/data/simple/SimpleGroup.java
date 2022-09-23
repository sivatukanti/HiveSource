// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.example.data.GroupValueSource;
import parquet.io.api.RecordConsumer;
import parquet.io.api.Binary;
import java.util.Iterator;
import parquet.schema.Type;
import java.util.ArrayList;
import java.util.List;
import parquet.schema.GroupType;
import parquet.example.data.Group;

public class SimpleGroup extends Group
{
    private final GroupType schema;
    private final List<Object>[] data;
    
    public SimpleGroup(final GroupType schema) {
        this.schema = schema;
        this.data = (List<Object>[])new List[schema.getFields().size()];
        for (int i = 0; i < schema.getFieldCount(); ++i) {
            this.data[i] = new ArrayList<Object>();
        }
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
    
    public String toString(final String indent) {
        String result = "";
        int i = 0;
        for (final Type field : this.schema.getFields()) {
            final String name = field.getName();
            final List<Object> values = this.data[i];
            ++i;
            if (values != null && values.size() > 0) {
                for (final Object value : values) {
                    result = result + indent + name;
                    if (value == null) {
                        result += ": NULL\n";
                    }
                    else if (value instanceof Group) {
                        result = result + "\n" + ((SimpleGroup)value).toString(indent + "  ");
                    }
                    else {
                        result = result + ": " + value.toString() + "\n";
                    }
                }
            }
        }
        return result;
    }
    
    @Override
    public Group addGroup(final int fieldIndex) {
        final SimpleGroup g = new SimpleGroup(this.schema.getType(fieldIndex).asGroupType());
        this.add(fieldIndex, g);
        return g;
    }
    
    @Override
    public Group getGroup(final int fieldIndex, final int index) {
        return (Group)this.getValue(fieldIndex, index);
    }
    
    private Object getValue(final int fieldIndex, final int index) {
        List<Object> list;
        try {
            list = this.data[fieldIndex];
        }
        catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("not found " + fieldIndex + "(" + this.schema.getFieldName(fieldIndex) + ") in group:\n" + this);
        }
        try {
            return list.get(index);
        }
        catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("not found " + fieldIndex + "(" + this.schema.getFieldName(fieldIndex) + ") element number " + index + " in group:\n" + this);
        }
    }
    
    private void add(final int fieldIndex, final Primitive value) {
        final Type type = this.schema.getType(fieldIndex);
        final List<Object> list = this.data[fieldIndex];
        if (!type.isRepetition(Type.Repetition.REPEATED) && !list.isEmpty()) {
            throw new IllegalStateException("field " + fieldIndex + " (" + type.getName() + ") can not have more than one value: " + list);
        }
        list.add(value);
    }
    
    @Override
    public int getFieldRepetitionCount(final int fieldIndex) {
        final List<Object> list = this.data[fieldIndex];
        return (list == null) ? 0 : list.size();
    }
    
    @Override
    public String getValueToString(final int fieldIndex, final int index) {
        return String.valueOf(this.getValue(fieldIndex, index));
    }
    
    @Override
    public String getString(final int fieldIndex, final int index) {
        return ((BinaryValue)this.getValue(fieldIndex, index)).getString();
    }
    
    @Override
    public int getInteger(final int fieldIndex, final int index) {
        return ((IntegerValue)this.getValue(fieldIndex, index)).getInteger();
    }
    
    @Override
    public long getLong(final int fieldIndex, final int index) {
        return ((LongValue)this.getValue(fieldIndex, index)).getLong();
    }
    
    @Override
    public double getDouble(final int fieldIndex, final int index) {
        return ((DoubleValue)this.getValue(fieldIndex, index)).getDouble();
    }
    
    @Override
    public float getFloat(final int fieldIndex, final int index) {
        return ((FloatValue)this.getValue(fieldIndex, index)).getFloat();
    }
    
    @Override
    public boolean getBoolean(final int fieldIndex, final int index) {
        return ((BooleanValue)this.getValue(fieldIndex, index)).getBoolean();
    }
    
    @Override
    public Binary getBinary(final int fieldIndex, final int index) {
        return ((BinaryValue)this.getValue(fieldIndex, index)).getBinary();
    }
    
    public NanoTime getTimeNanos(final int fieldIndex, final int index) {
        return NanoTime.fromInt96((Int96Value)this.getValue(fieldIndex, index));
    }
    
    @Override
    public Binary getInt96(final int fieldIndex, final int index) {
        return ((Int96Value)this.getValue(fieldIndex, index)).getInt96();
    }
    
    @Override
    public void add(final int fieldIndex, final int value) {
        this.add(fieldIndex, new IntegerValue(value));
    }
    
    @Override
    public void add(final int fieldIndex, final long value) {
        this.add(fieldIndex, new LongValue(value));
    }
    
    @Override
    public void add(final int fieldIndex, final String value) {
        this.add(fieldIndex, new BinaryValue(Binary.fromString(value)));
    }
    
    @Override
    public void add(final int fieldIndex, final NanoTime value) {
        this.add(fieldIndex, value.toInt96());
    }
    
    @Override
    public void add(final int fieldIndex, final boolean value) {
        this.add(fieldIndex, new BooleanValue(value));
    }
    
    @Override
    public void add(final int fieldIndex, final Binary value) {
        switch (this.getType().getType(fieldIndex).asPrimitiveType().getPrimitiveTypeName()) {
            case BINARY:
            case FIXED_LEN_BYTE_ARRAY: {
                this.add(fieldIndex, new BinaryValue(value));
                break;
            }
            case INT96: {
                this.add(fieldIndex, new Int96Value(value));
                break;
            }
            default: {
                throw new UnsupportedOperationException(this.getType().asPrimitiveType().getName() + " not supported for Binary");
            }
        }
    }
    
    @Override
    public void add(final int fieldIndex, final float value) {
        this.add(fieldIndex, new FloatValue(value));
    }
    
    @Override
    public void add(final int fieldIndex, final double value) {
        this.add(fieldIndex, new DoubleValue(value));
    }
    
    @Override
    public void add(final int fieldIndex, final Group value) {
        this.data[fieldIndex].add(value);
    }
    
    @Override
    public GroupType getType() {
        return this.schema;
    }
    
    @Override
    public void writeValue(final int field, final int index, final RecordConsumer recordConsumer) {
        ((Primitive)this.getValue(field, index)).writeValue(recordConsumer);
    }
}
