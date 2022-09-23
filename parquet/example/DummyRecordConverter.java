// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example;

import parquet.io.api.Binary;
import parquet.io.api.PrimitiveConverter;
import parquet.schema.PrimitiveType;
import parquet.schema.GroupType;
import java.util.List;
import parquet.io.api.Converter;
import parquet.schema.TypeConverter;
import parquet.schema.MessageType;
import parquet.io.api.GroupConverter;
import parquet.io.api.RecordMaterializer;

public final class DummyRecordConverter extends RecordMaterializer<Object>
{
    private Object a;
    private GroupConverter root;
    
    public DummyRecordConverter(final MessageType schema) {
        this.root = schema.convertWith((TypeConverter<GroupConverter>)new TypeConverter<Converter>() {
            @Override
            public Converter convertPrimitiveType(final List<GroupType> path, final PrimitiveType primitiveType) {
                return new PrimitiveConverter() {
                    @Override
                    public void addBinary(final Binary value) {
                        DummyRecordConverter.this.a = value;
                    }
                    
                    @Override
                    public void addBoolean(final boolean value) {
                        DummyRecordConverter.this.a = value;
                    }
                    
                    @Override
                    public void addDouble(final double value) {
                        DummyRecordConverter.this.a = value;
                    }
                    
                    @Override
                    public void addFloat(final float value) {
                        DummyRecordConverter.this.a = value;
                    }
                    
                    @Override
                    public void addInt(final int value) {
                        DummyRecordConverter.this.a = value;
                    }
                    
                    @Override
                    public void addLong(final long value) {
                        DummyRecordConverter.this.a = value;
                    }
                };
            }
            
            @Override
            public Converter convertGroupType(final List<GroupType> path, final GroupType groupType, final List<Converter> converters) {
                return new GroupConverter() {
                    @Override
                    public Converter getConverter(final int fieldIndex) {
                        return converters.get(fieldIndex);
                    }
                    
                    @Override
                    public void start() {
                        DummyRecordConverter.this.a = "start()";
                    }
                    
                    @Override
                    public void end() {
                        DummyRecordConverter.this.a = "end()";
                    }
                };
            }
            
            @Override
            public Converter convertMessageType(final MessageType messageType, final List<Converter> children) {
                return this.convertGroupType((List<GroupType>)null, (GroupType)messageType, children);
            }
        });
    }
    
    @Override
    public Object getCurrentRecord() {
        return this.a;
    }
    
    @Override
    public GroupConverter getRootConverter() {
        return this.root;
    }
}
