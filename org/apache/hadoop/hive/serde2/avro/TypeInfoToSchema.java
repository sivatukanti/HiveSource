// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import java.util.Arrays;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import java.util.Iterator;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.codehaus.jackson.JsonNode;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;

public class TypeInfoToSchema
{
    private long recordCounter;
    
    public TypeInfoToSchema() {
        this.recordCounter = 0L;
    }
    
    public Schema convert(final List<String> columnNames, final List<TypeInfo> columnTypes, final List<String> columnComments, final String namespace, String name, final String doc) {
        final List<Schema.Field> fields = new ArrayList<Schema.Field>();
        for (int i = 0; i < columnNames.size(); ++i) {
            final String comment = (columnComments.size() > i) ? columnComments.get(i) : null;
            final Schema.Field avroField = this.createAvroField(columnNames.get(i), columnTypes.get(i), comment);
            fields.addAll(this.getFields(avroField));
        }
        if (name == null || name.isEmpty()) {
            name = "baseRecord";
        }
        final Schema avroSchema = Schema.createRecord(name, doc, namespace, false);
        avroSchema.setFields(fields);
        return avroSchema;
    }
    
    private Schema.Field createAvroField(final String name, final TypeInfo typeInfo, final String comment) {
        return new Schema.Field(name, this.createAvroSchema(typeInfo), comment, null);
    }
    
    private Schema createAvroSchema(final TypeInfo typeInfo) {
        Schema schema = null;
        switch (typeInfo.getCategory()) {
            case PRIMITIVE: {
                schema = this.createAvroPrimitive(typeInfo);
                break;
            }
            case LIST: {
                schema = this.createAvroArray(typeInfo);
                break;
            }
            case MAP: {
                schema = this.createAvroMap(typeInfo);
                break;
            }
            case STRUCT: {
                schema = this.createAvroRecord(typeInfo);
                break;
            }
            case UNION: {
                schema = this.createAvroUnion(typeInfo);
                break;
            }
        }
        return this.wrapInUnionWithNull(schema);
    }
    
    private Schema createAvroPrimitive(final TypeInfo typeInfo) {
        final PrimitiveTypeInfo primitiveTypeInfo = (PrimitiveTypeInfo)typeInfo;
        Schema schema = null;
        switch (primitiveTypeInfo.getPrimitiveCategory()) {
            case STRING: {
                schema = Schema.create(Schema.Type.STRING);
                break;
            }
            case CHAR: {
                schema = AvroSerdeUtils.getSchemaFor("{\"type\":\"string\",\"logicalType\":\"char\",\"maxLength\":" + ((CharTypeInfo)typeInfo).getLength() + "}");
                break;
            }
            case VARCHAR: {
                schema = AvroSerdeUtils.getSchemaFor("{\"type\":\"string\",\"logicalType\":\"varchar\",\"maxLength\":" + ((VarcharTypeInfo)typeInfo).getLength() + "}");
                break;
            }
            case BINARY: {
                schema = Schema.create(Schema.Type.BYTES);
                break;
            }
            case BYTE: {
                schema = Schema.create(Schema.Type.INT);
                break;
            }
            case SHORT: {
                schema = Schema.create(Schema.Type.INT);
                break;
            }
            case INT: {
                schema = Schema.create(Schema.Type.INT);
                break;
            }
            case LONG: {
                schema = Schema.create(Schema.Type.LONG);
                break;
            }
            case FLOAT: {
                schema = Schema.create(Schema.Type.FLOAT);
                break;
            }
            case DOUBLE: {
                schema = Schema.create(Schema.Type.DOUBLE);
                break;
            }
            case BOOLEAN: {
                schema = Schema.create(Schema.Type.BOOLEAN);
                break;
            }
            case DECIMAL: {
                final DecimalTypeInfo decimalTypeInfo = (DecimalTypeInfo)typeInfo;
                final String precision = String.valueOf(decimalTypeInfo.precision());
                final String scale = String.valueOf(decimalTypeInfo.scale());
                schema = AvroSerdeUtils.getSchemaFor("{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":" + precision + "," + "\"scale\":" + scale + "}");
                break;
            }
            case DATE: {
                schema = AvroSerdeUtils.getSchemaFor("{\"type\":\"int\",\"logicalType\":\"date\"}");
                break;
            }
            case TIMESTAMP: {
                schema = AvroSerdeUtils.getSchemaFor("{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}");
                break;
            }
            case VOID: {
                schema = Schema.create(Schema.Type.NULL);
                break;
            }
            default: {
                throw new UnsupportedOperationException(typeInfo + " is not supported.");
            }
        }
        return schema;
    }
    
    private Schema createAvroUnion(final TypeInfo typeInfo) {
        final List<Schema> childSchemas = new ArrayList<Schema>();
        for (final TypeInfo childTypeInfo : ((UnionTypeInfo)typeInfo).getAllUnionObjectTypeInfos()) {
            final Schema childSchema = this.createAvroSchema(childTypeInfo);
            if (childSchema.getType() == Schema.Type.UNION) {
                childSchemas.addAll(childSchema.getTypes());
            }
            else {
                childSchemas.add(childSchema);
            }
        }
        return Schema.createUnion(this.removeDuplicateNullSchemas(childSchemas));
    }
    
    private Schema createAvroRecord(final TypeInfo typeInfo) {
        final List<Schema.Field> childFields = new ArrayList<Schema.Field>();
        final List<String> allStructFieldNames = ((StructTypeInfo)typeInfo).getAllStructFieldNames();
        final List<TypeInfo> allStructFieldTypeInfos = ((StructTypeInfo)typeInfo).getAllStructFieldTypeInfos();
        if (allStructFieldNames.size() != allStructFieldTypeInfos.size()) {
            throw new IllegalArgumentException("Failed to generate avro schema from hive schema. name and column type differs. names = " + allStructFieldNames + ", types = " + allStructFieldTypeInfos);
        }
        for (int i = 0; i < allStructFieldNames.size(); ++i) {
            final TypeInfo childTypeInfo = allStructFieldTypeInfos.get(i);
            final Schema.Field grandChildSchemaField = this.createAvroField(allStructFieldNames.get(i), childTypeInfo, childTypeInfo.toString());
            final List<Schema.Field> grandChildFields = this.getFields(grandChildSchemaField);
            childFields.addAll(grandChildFields);
        }
        final Schema recordSchema = Schema.createRecord("record_" + this.recordCounter, typeInfo.toString(), null, false);
        ++this.recordCounter;
        recordSchema.setFields(childFields);
        return recordSchema;
    }
    
    private Schema createAvroMap(final TypeInfo typeInfo) {
        final TypeInfo keyTypeInfo = ((MapTypeInfo)typeInfo).getMapKeyTypeInfo();
        if (((PrimitiveTypeInfo)keyTypeInfo).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UnsupportedOperationException("Key of Map can only be a String");
        }
        final TypeInfo valueTypeInfo = ((MapTypeInfo)typeInfo).getMapValueTypeInfo();
        final Schema valueSchema = this.createAvroSchema(valueTypeInfo);
        return Schema.createMap(valueSchema);
    }
    
    private Schema createAvroArray(final TypeInfo typeInfo) {
        final ListTypeInfo listTypeInfo = (ListTypeInfo)typeInfo;
        final Schema listSchema = this.createAvroSchema(listTypeInfo.getListElementTypeInfo());
        return Schema.createArray(listSchema);
    }
    
    private List<Schema.Field> getFields(final Schema.Field schemaField) {
        final List<Schema.Field> fields = new ArrayList<Schema.Field>();
        final JsonNode nullDefault = JsonNodeFactory.instance.nullNode();
        if (schemaField.schema().getType() == Schema.Type.RECORD) {
            for (final Schema.Field field : schemaField.schema().getFields()) {
                fields.add(new Schema.Field(field.name(), field.schema(), field.doc(), nullDefault));
            }
        }
        else {
            fields.add(new Schema.Field(schemaField.name(), schemaField.schema(), schemaField.doc(), nullDefault));
        }
        return fields;
    }
    
    private Schema wrapInUnionWithNull(final Schema schema) {
        Schema wrappedSchema = schema;
        switch (schema.getType()) {
            case NULL: {
                break;
            }
            case UNION: {
                final List<Schema> existingSchemas = this.removeDuplicateNullSchemas(schema.getTypes());
                wrappedSchema = Schema.createUnion(existingSchemas);
                break;
            }
            default: {
                wrappedSchema = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), schema));
                break;
            }
        }
        return wrappedSchema;
    }
    
    private List<Schema> removeDuplicateNullSchemas(final List<Schema> childSchemas) {
        final List<Schema> prunedSchemas = new ArrayList<Schema>();
        boolean isNullPresent = false;
        for (final Schema schema : childSchemas) {
            if (schema.getType() == Schema.Type.NULL) {
                isNullPresent = true;
            }
            else {
                prunedSchemas.add(schema);
            }
        }
        if (isNullPresent) {
            prunedSchemas.add(0, Schema.create(Schema.Type.NULL));
        }
        return prunedSchemas;
    }
}
