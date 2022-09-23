// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import java.util.Iterator;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;

public class AvroObjectInspectorGenerator
{
    private final List<String> columnNames;
    private final List<TypeInfo> columnTypes;
    private final List<String> columnComments;
    private final ObjectInspector oi;
    
    public AvroObjectInspectorGenerator(final Schema schema) throws SerDeException {
        this.verifySchemaIsARecord(schema);
        this.columnNames = generateColumnNames(schema);
        this.columnTypes = SchemaToTypeInfo.generateColumnTypes(schema);
        this.columnComments = generateColumnComments(schema);
        assert this.columnNames.size() == this.columnTypes.size();
        this.oi = this.createObjectInspector();
    }
    
    private void verifySchemaIsARecord(final Schema schema) throws SerDeException {
        if (!schema.getType().equals(Schema.Type.RECORD)) {
            throw new AvroSerdeException("Schema for table must be of type RECORD. Received type: " + schema.getType());
        }
    }
    
    public List<String> getColumnNames() {
        return this.columnNames;
    }
    
    public List<TypeInfo> getColumnTypes() {
        return this.columnTypes;
    }
    
    public ObjectInspector getObjectInspector() {
        return this.oi;
    }
    
    private ObjectInspector createObjectInspector() throws SerDeException {
        final List<ObjectInspector> columnOIs = new ArrayList<ObjectInspector>(this.columnNames.size());
        for (int i = 0; i < this.columnNames.size(); ++i) {
            columnOIs.add(i, this.createObjectInspectorWorker(this.columnTypes.get(i)));
        }
        return ObjectInspectorFactory.getStandardStructObjectInspector(this.columnNames, columnOIs, this.columnComments);
    }
    
    private ObjectInspector createObjectInspectorWorker(final TypeInfo ti) throws SerDeException {
        if (!this.supportedCategories(ti)) {
            throw new AvroSerdeException("Don't yet support this type: " + ti);
        }
        ObjectInspector result = null;
        switch (ti.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveTypeInfo pti = (PrimitiveTypeInfo)ti;
                result = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(pti);
                break;
            }
            case STRUCT: {
                final StructTypeInfo sti = (StructTypeInfo)ti;
                final ArrayList<ObjectInspector> ois = new ArrayList<ObjectInspector>(sti.getAllStructFieldTypeInfos().size());
                for (final TypeInfo typeInfo : sti.getAllStructFieldTypeInfos()) {
                    ois.add(this.createObjectInspectorWorker(typeInfo));
                }
                result = ObjectInspectorFactory.getStandardStructObjectInspector(sti.getAllStructFieldNames(), ois);
                break;
            }
            case MAP: {
                final MapTypeInfo mti = (MapTypeInfo)ti;
                result = ObjectInspectorFactory.getStandardMapObjectInspector(PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.STRING), this.createObjectInspectorWorker(mti.getMapValueTypeInfo()));
                break;
            }
            case LIST: {
                final ListTypeInfo ati = (ListTypeInfo)ti;
                result = ObjectInspectorFactory.getStandardListObjectInspector(this.createObjectInspectorWorker(ati.getListElementTypeInfo()));
                break;
            }
            case UNION: {
                final UnionTypeInfo uti = (UnionTypeInfo)ti;
                final List<TypeInfo> allUnionObjectTypeInfos = uti.getAllUnionObjectTypeInfos();
                final List<ObjectInspector> unionObjectInspectors = new ArrayList<ObjectInspector>(allUnionObjectTypeInfos.size());
                for (final TypeInfo typeInfo2 : allUnionObjectTypeInfos) {
                    unionObjectInspectors.add(this.createObjectInspectorWorker(typeInfo2));
                }
                result = ObjectInspectorFactory.getStandardUnionObjectInspector(unionObjectInspectors);
                break;
            }
            default: {
                throw new AvroSerdeException("No Hive categories matched: " + ti);
            }
        }
        return result;
    }
    
    private boolean supportedCategories(final TypeInfo ti) {
        final ObjectInspector.Category c = ti.getCategory();
        return c.equals(ObjectInspector.Category.PRIMITIVE) || c.equals(ObjectInspector.Category.MAP) || c.equals(ObjectInspector.Category.LIST) || c.equals(ObjectInspector.Category.STRUCT) || c.equals(ObjectInspector.Category.UNION);
    }
    
    public static List<String> generateColumnNames(final Schema schema) {
        final List<Schema.Field> fields = schema.getFields();
        final List<String> fieldsList = new ArrayList<String>(fields.size());
        for (final Schema.Field field : fields) {
            fieldsList.add(field.name());
        }
        return fieldsList;
    }
    
    public static List<String> generateColumnComments(final Schema schema) {
        final List<Schema.Field> fields = schema.getFields();
        final List<String> fieldComments = new ArrayList<String>(fields.size());
        for (final Schema.Field field : fields) {
            final String fieldComment = (field.doc() == null) ? "" : field.doc();
            fieldComments.add(fieldComment);
        }
        return fieldComments;
    }
}
