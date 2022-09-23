// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.avro.generic.GenericData;
import java.util.Set;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.Schema;
import org.apache.commons.logging.Log;

class AvroSerializer
{
    private static final Log LOG;
    private static final Schema STRING_SCHEMA;
    AvroGenericRecordWritable cache;
    final InstanceCache<Schema, InstanceCache<Object, GenericEnumSymbol>> enums;
    
    AvroSerializer() {
        this.cache = new AvroGenericRecordWritable();
        this.enums = new InstanceCache<Schema, InstanceCache<Object, GenericEnumSymbol>>() {
            @Override
            protected InstanceCache<Object, GenericEnumSymbol> makeInstance(final Schema schema, final Set<Schema> seenSchemas) {
                return new InstanceCache<Object, GenericEnumSymbol>() {
                    @Override
                    protected GenericEnumSymbol makeInstance(final Object seed, final Set<Object> seenSchemas) {
                        return new GenericData.EnumSymbol(schema, seed.toString());
                    }
                };
            }
        };
    }
    
    public Writable serialize(final Object o, final ObjectInspector objectInspector, final List<String> columnNames, final List<TypeInfo> columnTypes, final Schema schema) throws AvroSerdeException {
        final StructObjectInspector soi = (StructObjectInspector)objectInspector;
        final GenericData.Record record = new GenericData.Record(schema);
        final List<? extends StructField> outputFieldRefs = soi.getAllStructFieldRefs();
        if (outputFieldRefs.size() != columnNames.size()) {
            throw new AvroSerdeException("Number of input columns was different than output columns (in = " + columnNames.size() + " vs out = " + outputFieldRefs.size());
        }
        final int size = schema.getFields().size();
        if (outputFieldRefs.size() != size) {
            throw new AvroSerdeException("Hive passed in a different number of fields than the schema expected: (Hive wanted " + outputFieldRefs.size() + ", Avro expected " + schema.getFields().size());
        }
        final List<? extends StructField> allStructFieldRefs = soi.getAllStructFieldRefs();
        final List<Object> structFieldsDataAsList = soi.getStructFieldsDataAsList(o);
        for (int i = 0; i < size; ++i) {
            final Schema.Field field = schema.getFields().get(i);
            final TypeInfo typeInfo = columnTypes.get(i);
            final StructField structFieldRef = (StructField)allStructFieldRefs.get(i);
            final Object structFieldData = structFieldsDataAsList.get(i);
            final ObjectInspector fieldOI = structFieldRef.getFieldObjectInspector();
            final Object val = this.serialize(typeInfo, fieldOI, structFieldData, field.schema());
            record.put(field.name(), val);
        }
        if (!GenericData.get().validate(schema, record)) {
            throw new SerializeToAvroException(schema, record);
        }
        this.cache.setRecord(record);
        return this.cache;
    }
    
    private Object serialize(final TypeInfo typeInfo, final ObjectInspector fieldOI, final Object structFieldData, Schema schema) throws AvroSerdeException {
        if (null == structFieldData) {
            return null;
        }
        if (AvroSerdeUtils.isNullableType(schema)) {
            schema = AvroSerdeUtils.getOtherTypeFromNullableType(schema);
        }
        if (Schema.Type.ENUM.equals(schema.getType())) {
            assert fieldOI instanceof PrimitiveObjectInspector;
            return this.serializeEnum(typeInfo, (PrimitiveObjectInspector)fieldOI, structFieldData, schema);
        }
        else {
            switch (typeInfo.getCategory()) {
                case PRIMITIVE: {
                    assert fieldOI instanceof PrimitiveObjectInspector;
                    return this.serializePrimitive(typeInfo, (PrimitiveObjectInspector)fieldOI, structFieldData, schema);
                }
                case MAP: {
                    assert fieldOI instanceof MapObjectInspector;
                    assert typeInfo instanceof MapTypeInfo;
                    return this.serializeMap((MapTypeInfo)typeInfo, (MapObjectInspector)fieldOI, structFieldData, schema);
                }
                case LIST: {
                    assert fieldOI instanceof ListObjectInspector;
                    assert typeInfo instanceof ListTypeInfo;
                    return this.serializeList((ListTypeInfo)typeInfo, (ListObjectInspector)fieldOI, structFieldData, schema);
                }
                case UNION: {
                    assert fieldOI instanceof UnionObjectInspector;
                    assert typeInfo instanceof UnionTypeInfo;
                    return this.serializeUnion((UnionTypeInfo)typeInfo, (UnionObjectInspector)fieldOI, structFieldData, schema);
                }
                case STRUCT: {
                    assert fieldOI instanceof StructObjectInspector;
                    assert typeInfo instanceof StructTypeInfo;
                    return this.serializeStruct((StructTypeInfo)typeInfo, (StructObjectInspector)fieldOI, structFieldData, schema);
                }
                default: {
                    throw new AvroSerdeException("Ran out of TypeInfo Categories: " + typeInfo.getCategory());
                }
            }
        }
    }
    
    private Object serializeEnum(final TypeInfo typeInfo, final PrimitiveObjectInspector fieldOI, final Object structFieldData, final Schema schema) throws AvroSerdeException {
        return this.enums.retrieve(schema).retrieve(this.serializePrimitive(typeInfo, fieldOI, structFieldData, schema));
    }
    
    private Object serializeStruct(final StructTypeInfo typeInfo, final StructObjectInspector ssoi, final Object o, final Schema schema) throws AvroSerdeException {
        final int size = schema.getFields().size();
        final List<? extends StructField> allStructFieldRefs = ssoi.getAllStructFieldRefs();
        final List<Object> structFieldsDataAsList = ssoi.getStructFieldsDataAsList(o);
        final GenericData.Record record = new GenericData.Record(schema);
        final ArrayList<TypeInfo> allStructFieldTypeInfos = typeInfo.getAllStructFieldTypeInfos();
        for (int i = 0; i < size; ++i) {
            final Schema.Field field = schema.getFields().get(i);
            final TypeInfo colTypeInfo = allStructFieldTypeInfos.get(i);
            final StructField structFieldRef = (StructField)allStructFieldRefs.get(i);
            final Object structFieldData = structFieldsDataAsList.get(i);
            final ObjectInspector fieldOI = structFieldRef.getFieldObjectInspector();
            final Object val = this.serialize(colTypeInfo, fieldOI, structFieldData, field.schema());
            record.put(field.name(), val);
        }
        return record;
    }
    
    private Object serializePrimitive(final TypeInfo typeInfo, final PrimitiveObjectInspector fieldOI, final Object structFieldData, final Schema schema) throws AvroSerdeException {
        switch (fieldOI.getPrimitiveCategory()) {
            case BINARY: {
                if (schema.getType() == Schema.Type.BYTES) {
                    return AvroSerdeUtils.getBufferFromBytes((byte[])fieldOI.getPrimitiveJavaObject(structFieldData));
                }
                if (schema.getType() == Schema.Type.FIXED) {
                    final GenericData.Fixed fixed = new GenericData.Fixed(schema, (byte[])fieldOI.getPrimitiveJavaObject(structFieldData));
                    return fixed;
                }
                throw new AvroSerdeException("Unexpected Avro schema for Binary TypeInfo: " + schema.getType());
            }
            case DECIMAL: {
                final HiveDecimal dec = (HiveDecimal)fieldOI.getPrimitiveJavaObject(structFieldData);
                return AvroSerdeUtils.getBufferFromDecimal(dec, ((DecimalTypeInfo)typeInfo).scale());
            }
            case CHAR: {
                final HiveChar ch = (HiveChar)fieldOI.getPrimitiveJavaObject(structFieldData);
                return ch.getStrippedValue();
            }
            case VARCHAR: {
                final HiveVarchar vc = (HiveVarchar)fieldOI.getPrimitiveJavaObject(structFieldData);
                return vc.getValue();
            }
            case DATE: {
                final Date date = ((DateObjectInspector)fieldOI).getPrimitiveJavaObject(structFieldData);
                return DateWritable.dateToDays(date);
            }
            case TIMESTAMP: {
                final Timestamp timestamp = ((TimestampObjectInspector)fieldOI).getPrimitiveJavaObject(structFieldData);
                return timestamp.getTime();
            }
            case UNKNOWN: {
                throw new AvroSerdeException("Received UNKNOWN primitive category.");
            }
            case VOID: {
                return null;
            }
            default: {
                return fieldOI.getPrimitiveJavaObject(structFieldData);
            }
        }
    }
    
    private Object serializeUnion(final UnionTypeInfo typeInfo, final UnionObjectInspector fieldOI, final Object structFieldData, final Schema schema) throws AvroSerdeException {
        final byte tag = fieldOI.getTag(structFieldData);
        return this.serialize(typeInfo.getAllUnionObjectTypeInfos().get(tag), fieldOI.getObjectInspectors().get(tag), fieldOI.getField(structFieldData), schema.getTypes().get(tag));
    }
    
    private Object serializeList(final ListTypeInfo typeInfo, final ListObjectInspector fieldOI, final Object structFieldData, final Schema schema) throws AvroSerdeException {
        final List<?> list = fieldOI.getList(structFieldData);
        final List<Object> deserialized = new GenericData.Array<Object>(list.size(), schema);
        final TypeInfo listElementTypeInfo = typeInfo.getListElementTypeInfo();
        final ObjectInspector listElementObjectInspector = fieldOI.getListElementObjectInspector();
        final Schema elementType = schema.getElementType();
        for (int i = 0; i < list.size(); ++i) {
            deserialized.add(i, this.serialize(listElementTypeInfo, listElementObjectInspector, list.get(i), elementType));
        }
        return deserialized;
    }
    
    private Object serializeMap(final MapTypeInfo typeInfo, final MapObjectInspector fieldOI, final Object structFieldData, final Schema schema) throws AvroSerdeException {
        if (!this.mapHasStringKey(fieldOI.getMapKeyObjectInspector())) {
            throw new AvroSerdeException("Avro only supports maps with keys as Strings.  Current Map is: " + typeInfo.toString());
        }
        final ObjectInspector mapKeyObjectInspector = fieldOI.getMapKeyObjectInspector();
        final ObjectInspector mapValueObjectInspector = fieldOI.getMapValueObjectInspector();
        final TypeInfo mapKeyTypeInfo = typeInfo.getMapKeyTypeInfo();
        final TypeInfo mapValueTypeInfo = typeInfo.getMapValueTypeInfo();
        final Map<?, ?> map = fieldOI.getMap(structFieldData);
        final Schema valueType = schema.getValueType();
        final Map<Object, Object> deserialized = new HashMap<Object, Object>(fieldOI.getMapSize(structFieldData));
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            deserialized.put(this.serialize(mapKeyTypeInfo, mapKeyObjectInspector, entry.getKey(), AvroSerializer.STRING_SCHEMA), this.serialize(mapValueTypeInfo, mapValueObjectInspector, entry.getValue(), valueType));
        }
        return deserialized;
    }
    
    private boolean mapHasStringKey(final ObjectInspector mapKeyObjectInspector) {
        return mapKeyObjectInspector instanceof PrimitiveObjectInspector && ((PrimitiveObjectInspector)mapKeyObjectInspector).getPrimitiveCategory().equals(PrimitiveObjectInspector.PrimitiveCategory.STRING);
    }
    
    static {
        LOG = LogFactory.getLog(AvroSerializer.class);
        STRING_SCHEMA = Schema.create(Schema.Type.STRING);
    }
    
    public static class SerializeToAvroException extends AvroSerdeException
    {
        private final Schema schema;
        private final GenericData.Record record;
        
        public SerializeToAvroException(final Schema schema, final GenericData.Record record) {
            this.schema = schema;
            this.record = record;
        }
        
        @Override
        public String toString() {
            return "Avro could not validate record against schema (record = " + this.record + ") (schema = " + this.schema.toString(false) + ")";
        }
    }
}
