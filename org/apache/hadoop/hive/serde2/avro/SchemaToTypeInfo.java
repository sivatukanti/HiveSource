// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import java.util.IdentityHashMap;
import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import java.util.Hashtable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.avro.Schema;
import java.util.Map;

class SchemaToTypeInfo
{
    private static final Map<Schema.Type, TypeInfo> primitiveTypeToTypeInfo;
    static InstanceCache<Schema, TypeInfo> typeInfoCache;
    
    private static Map<Schema.Type, TypeInfo> initTypeMap() {
        final Map<Schema.Type, TypeInfo> theMap = new Hashtable<Schema.Type, TypeInfo>();
        theMap.put(Schema.Type.NULL, TypeInfoFactory.getPrimitiveTypeInfo("void"));
        theMap.put(Schema.Type.BOOLEAN, TypeInfoFactory.getPrimitiveTypeInfo("boolean"));
        theMap.put(Schema.Type.INT, TypeInfoFactory.getPrimitiveTypeInfo("int"));
        theMap.put(Schema.Type.LONG, TypeInfoFactory.getPrimitiveTypeInfo("bigint"));
        theMap.put(Schema.Type.FLOAT, TypeInfoFactory.getPrimitiveTypeInfo("float"));
        theMap.put(Schema.Type.DOUBLE, TypeInfoFactory.getPrimitiveTypeInfo("double"));
        theMap.put(Schema.Type.BYTES, TypeInfoFactory.getPrimitiveTypeInfo("binary"));
        theMap.put(Schema.Type.FIXED, TypeInfoFactory.getPrimitiveTypeInfo("binary"));
        theMap.put(Schema.Type.STRING, TypeInfoFactory.getPrimitiveTypeInfo("string"));
        return Collections.unmodifiableMap((Map<? extends Schema.Type, ? extends TypeInfo>)theMap);
    }
    
    public static List<TypeInfo> generateColumnTypes(final Schema schema) throws AvroSerdeException {
        return generateColumnTypes(schema, null);
    }
    
    public static List<TypeInfo> generateColumnTypes(final Schema schema, final Set<Schema> seenSchemas) throws AvroSerdeException {
        final List<Schema.Field> fields = schema.getFields();
        final List<TypeInfo> types = new ArrayList<TypeInfo>(fields.size());
        for (final Schema.Field field : fields) {
            types.add(generateTypeInfo(field.schema(), seenSchemas));
        }
        return types;
    }
    
    public static TypeInfo generateTypeInfo(final Schema schema, final Set<Schema> seenSchemas) throws AvroSerdeException {
        final Schema.Type type = schema.getType();
        if (type == Schema.Type.BYTES && "decimal".equalsIgnoreCase(schema.getProp("logicalType"))) {
            int precision = 0;
            int scale = 0;
            try {
                precision = schema.getJsonProp("precision").getIntValue();
                scale = schema.getJsonProp("scale").getIntValue();
            }
            catch (Exception ex) {
                throw new AvroSerdeException("Failed to obtain scale value from file schema: " + schema, ex);
            }
            try {
                HiveDecimalUtils.validateParameter(precision, scale);
            }
            catch (Exception ex) {
                throw new AvroSerdeException("Invalid precision or scale for decimal type", ex);
            }
            return TypeInfoFactory.getDecimalTypeInfo(precision, scale);
        }
        if (type == Schema.Type.STRING && "char".equalsIgnoreCase(schema.getProp("logicalType"))) {
            int maxLength = 0;
            try {
                maxLength = schema.getJsonProp("maxLength").getValueAsInt();
            }
            catch (Exception ex2) {
                throw new AvroSerdeException("Failed to obtain maxLength value from file schema: " + schema, ex2);
            }
            return TypeInfoFactory.getCharTypeInfo(maxLength);
        }
        if (type == Schema.Type.STRING && "varchar".equalsIgnoreCase(schema.getProp("logicalType"))) {
            int maxLength = 0;
            try {
                maxLength = schema.getJsonProp("maxLength").getValueAsInt();
            }
            catch (Exception ex2) {
                throw new AvroSerdeException("Failed to obtain maxLength value from file schema: " + schema, ex2);
            }
            return TypeInfoFactory.getVarcharTypeInfo(maxLength);
        }
        if (type == Schema.Type.INT && "date".equals(schema.getProp("logicalType"))) {
            return TypeInfoFactory.dateTypeInfo;
        }
        if (type == Schema.Type.LONG && "timestamp-millis".equals(schema.getProp("logicalType"))) {
            return TypeInfoFactory.timestampTypeInfo;
        }
        return SchemaToTypeInfo.typeInfoCache.retrieve(schema, seenSchemas);
    }
    
    private static TypeInfo generateTypeInfoWorker(final Schema schema, final Set<Schema> seenSchemas) throws AvroSerdeException {
        if (AvroSerdeUtils.isNullableType(schema)) {
            return generateTypeInfo(AvroSerdeUtils.getOtherTypeFromNullableType(schema), seenSchemas);
        }
        final Schema.Type type = schema.getType();
        if (SchemaToTypeInfo.primitiveTypeToTypeInfo.containsKey(type)) {
            return SchemaToTypeInfo.primitiveTypeToTypeInfo.get(type);
        }
        switch (type) {
            case RECORD: {
                return generateRecordTypeInfo(schema, seenSchemas);
            }
            case MAP: {
                return generateMapTypeInfo(schema, seenSchemas);
            }
            case ARRAY: {
                return generateArrayTypeInfo(schema, seenSchemas);
            }
            case UNION: {
                return generateUnionTypeInfo(schema, seenSchemas);
            }
            case ENUM: {
                return generateEnumTypeInfo(schema);
            }
            default: {
                throw new AvroSerdeException("Do not yet support: " + schema);
            }
        }
    }
    
    private static TypeInfo generateRecordTypeInfo(final Schema schema, Set<Schema> seenSchemas) throws AvroSerdeException {
        assert schema.getType().equals(Schema.Type.RECORD);
        if (seenSchemas == null) {
            seenSchemas = Collections.newSetFromMap(new IdentityHashMap<Schema, Boolean>());
        }
        else if (seenSchemas.contains(schema)) {
            return SchemaToTypeInfo.primitiveTypeToTypeInfo.get(Schema.Type.NULL);
        }
        seenSchemas.add(schema);
        final List<Schema.Field> fields = schema.getFields();
        final List<String> fieldNames = new ArrayList<String>(fields.size());
        final List<TypeInfo> typeInfos = new ArrayList<TypeInfo>(fields.size());
        for (int i = 0; i < fields.size(); ++i) {
            fieldNames.add(i, fields.get(i).name());
            typeInfos.add(i, generateTypeInfo(fields.get(i).schema(), seenSchemas));
        }
        return TypeInfoFactory.getStructTypeInfo(fieldNames, typeInfos);
    }
    
    private static TypeInfo generateMapTypeInfo(final Schema schema, final Set<Schema> seenSchemas) throws AvroSerdeException {
        assert schema.getType().equals(Schema.Type.MAP);
        final Schema valueType = schema.getValueType();
        final TypeInfo ti = generateTypeInfo(valueType, seenSchemas);
        return TypeInfoFactory.getMapTypeInfo(TypeInfoFactory.getPrimitiveTypeInfo("string"), ti);
    }
    
    private static TypeInfo generateArrayTypeInfo(final Schema schema, final Set<Schema> seenSchemas) throws AvroSerdeException {
        assert schema.getType().equals(Schema.Type.ARRAY);
        final Schema itemsType = schema.getElementType();
        final TypeInfo itemsTypeInfo = generateTypeInfo(itemsType, seenSchemas);
        return TypeInfoFactory.getListTypeInfo(itemsTypeInfo);
    }
    
    private static TypeInfo generateUnionTypeInfo(final Schema schema, final Set<Schema> seenSchemas) throws AvroSerdeException {
        assert schema.getType().equals(Schema.Type.UNION);
        final List<Schema> types = schema.getTypes();
        final List<TypeInfo> typeInfos = new ArrayList<TypeInfo>(types.size());
        for (final Schema type : types) {
            typeInfos.add(generateTypeInfo(type, seenSchemas));
        }
        return TypeInfoFactory.getUnionTypeInfo(typeInfos);
    }
    
    private static TypeInfo generateEnumTypeInfo(final Schema schema) {
        assert schema.getType().equals(Schema.Type.ENUM);
        return TypeInfoFactory.getPrimitiveTypeInfo("string");
    }
    
    static {
        primitiveTypeToTypeInfo = initTypeMap();
        SchemaToTypeInfo.typeInfoCache = new InstanceCache<Schema, TypeInfo>() {
            @Override
            protected TypeInfo makeInstance(final Schema s, final Set<Schema> seenSchemas) throws AvroSerdeException {
                return generateTypeInfoWorker(s, seenSchemas);
            }
        };
    }
}
