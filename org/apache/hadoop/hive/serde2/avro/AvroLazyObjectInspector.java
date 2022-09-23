// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.ClassUtils;
import org.apache.hadoop.hive.serde2.lazy.LazyUnion;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyUnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.LazyArray;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyFactory;
import org.apache.hadoop.hive.serde2.lazy.LazyObject;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyMapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardUnionObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyListObjectInspector;
import org.apache.avro.io.DatumReader;
import java.io.InputStream;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericDatumReader;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.io.Writable;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.BaseStructObjectInspector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.hive.serde2.lazy.LazyMap;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;

public class AvroLazyObjectInspector extends LazySimpleStructObjectInspector
{
    private Schema readerSchema;
    private AvroSchemaRetriever schemaRetriever;
    public static final Log LOG;
    
    @Deprecated
    public AvroLazyObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) {
        super(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, nullSequence, lastColumnTakesRest, escaped, escapeChar);
    }
    
    public AvroLazyObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        super(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, lazyParams);
    }
    
    public void setReaderSchema(final Schema readerSchema) {
        this.readerSchema = readerSchema;
    }
    
    public void setSchemaRetriever(final AvroSchemaRetriever schemaRetriever) {
        this.schemaRetriever = schemaRetriever;
    }
    
    @Override
    public Object getStructFieldData(final Object data, final StructField f) {
        if (data == null) {
            return null;
        }
        final int fieldID = f.getFieldID();
        if (AvroLazyObjectInspector.LOG.isDebugEnabled()) {
            AvroLazyObjectInspector.LOG.debug("Getting struct field data for field: [" + f.getFieldName() + "] on data [" + data.getClass() + "]");
        }
        if (data instanceof LazyStruct) {
            final LazyStruct row = (LazyStruct)data;
            final Object rowField = row.getField(fieldID);
            if (rowField instanceof LazyStruct) {
                if (AvroLazyObjectInspector.LOG.isDebugEnabled()) {
                    AvroLazyObjectInspector.LOG.debug("Deserializing struct [" + rowField.getClass() + "]");
                }
                return this.deserializeStruct(rowField, f.getFieldName());
            }
            if (rowField instanceof LazyMap) {
                final LazyMap lazyMap = (LazyMap)rowField;
                for (final Map.Entry<Object, Object> entry : lazyMap.getMap().entrySet()) {
                    final Object _key = entry.getKey();
                    final Object _value = entry.getValue();
                    if (_value instanceof LazyStruct) {
                        lazyMap.getMap().put(_key, this.deserializeStruct(_value, f.getFieldName()));
                    }
                }
                if (AvroLazyObjectInspector.LOG.isDebugEnabled()) {
                    AvroLazyObjectInspector.LOG.debug("Returning a lazy map for field [" + f.getFieldName() + "]");
                }
                return lazyMap;
            }
            if (AvroLazyObjectInspector.LOG.isDebugEnabled()) {
                AvroLazyObjectInspector.LOG.debug("Returning [" + rowField.toString() + "] for field [" + f.getFieldName() + "]");
            }
            return rowField;
        }
        else {
            if (!(data instanceof List)) {
                throw new IllegalArgumentException("data should be an instance of list");
            }
            if (fieldID >= ((List)data).size()) {
                return null;
            }
            final Object field = ((List)data).get(fieldID);
            if (field == null) {
                return null;
            }
            return this.toLazyObject(field, f.getFieldObjectInspector());
        }
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(final Object data) {
        if (data == null) {
            return null;
        }
        final List<Object> result = new ArrayList<Object>(this.fields.size());
        for (int i = 0; i < this.fields.size(); ++i) {
            result.add(this.getStructFieldData(data, this.fields.get(i)));
        }
        return result;
    }
    
    private Object deserializeStruct(final Object struct, final String fieldName) {
        final byte[] data = ((LazyStruct)struct).getBytes();
        final AvroDeserializer deserializer = new AvroDeserializer();
        if (data == null) {
            return null;
        }
        if (this.readerSchema == null && this.schemaRetriever == null) {
            throw new IllegalArgumentException("reader schema or schemaRetriever must be set for field [" + fieldName + "]");
        }
        Schema ws = null;
        Schema rs = null;
        int offset = 0;
        final AvroGenericRecordWritable avroWritable = new AvroGenericRecordWritable();
        Label_0445: {
            if (this.readerSchema == null) {
                rs = this.schemaRetriever.retrieveReaderSchema(data);
                if (rs == null) {
                    throw new IllegalStateException("A valid reader schema could not be retrieved either directly or from the schema retriever for field [" + fieldName + "]");
                }
                ws = this.schemaRetriever.retrieveWriterSchema(data);
                if (ws == null) {
                    throw new IllegalStateException("Null writer schema retrieved from schemaRetriever for field [" + fieldName + "]");
                }
                offset = this.schemaRetriever.getOffset();
                if (data.length < offset) {
                    throw new IllegalArgumentException("Data size cannot be less than [" + offset + "]. Found [" + data.length + "]");
                }
                if (AvroLazyObjectInspector.LOG.isDebugEnabled()) {
                    AvroLazyObjectInspector.LOG.debug("Retrieved writer Schema: " + ws.toString());
                    AvroLazyObjectInspector.LOG.debug("Retrieved reader Schema: " + rs.toString());
                }
                try {
                    avroWritable.readFields(data, offset, data.length, ws, rs);
                    break Label_0445;
                }
                catch (IOException ioe) {
                    throw new AvroObjectInspectorException("Error deserializing avro payload", ioe);
                }
            }
            if (this.schemaRetriever != null) {
                ws = this.schemaRetriever.retrieveWriterSchema(data);
                if (ws == null) {
                    throw new IllegalStateException("Null writer schema retrieved from schemaRetriever for field [" + fieldName + "]");
                }
            }
            else {
                ws = this.retrieveSchemaFromBytes(data);
            }
            rs = this.readerSchema;
            try {
                avroWritable.readFields(data, ws, rs);
            }
            catch (IOException ioe) {
                throw new AvroObjectInspectorException("Error deserializing avro payload", ioe);
            }
        }
        AvroObjectInspectorGenerator oiGenerator = null;
        Object deserializedObject = null;
        try {
            oiGenerator = new AvroObjectInspectorGenerator(rs);
            deserializedObject = deserializer.deserialize(oiGenerator.getColumnNames(), oiGenerator.getColumnTypes(), avroWritable, rs);
        }
        catch (SerDeException se) {
            throw new AvroObjectInspectorException("Error deserializing avro payload", se);
        }
        return deserializedObject;
    }
    
    private Schema retrieveSchemaFromBytes(final byte[] data) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
        Schema schema = null;
        try {
            final DataFileStream<GenericRecord> dfs = new DataFileStream<GenericRecord>(bais, reader);
            schema = dfs.getSchema();
        }
        catch (IOException ioe) {
            throw new AvroObjectInspectorException("An error occurred retrieving schema from bytes", ioe);
        }
        return schema;
    }
    
    private Object toLazyObject(final Object field, final ObjectInspector fieldOI) {
        if (this.isPrimitive(field.getClass())) {
            return this.toLazyPrimitiveObject(field, fieldOI);
        }
        if (fieldOI instanceof LazyListObjectInspector) {
            return this.toLazyListObject(field, fieldOI);
        }
        if (field instanceof StandardUnionObjectInspector.StandardUnion) {
            return this.toLazyUnionObject(field, fieldOI);
        }
        if (fieldOI instanceof LazyMapObjectInspector) {
            return this.toLazyMapObject(field, fieldOI);
        }
        return field;
    }
    
    private LazyObject<? extends ObjectInspector> toLazyPrimitiveObject(final Object obj, final ObjectInspector oi) {
        if (obj == null) {
            return null;
        }
        final LazyObject<? extends ObjectInspector> lazyObject = LazyFactory.createLazyObject(oi);
        final ByteArrayRef ref = new ByteArrayRef();
        final String objAsString = obj.toString().trim();
        ref.setData(objAsString.getBytes());
        lazyObject.init(ref, 0, ref.getData().length);
        return lazyObject;
    }
    
    private Object toLazyListObject(final Object obj, final ObjectInspector objectInspector) {
        if (obj == null) {
            return null;
        }
        final List<?> listObj = (List<?>)obj;
        final LazyArray retList = (LazyArray)LazyFactory.createLazyObject(objectInspector);
        final List<Object> lazyList = retList.getList();
        final ObjectInspector listElementOI = ((ListObjectInspector)objectInspector).getListElementObjectInspector();
        for (int i = 0; i < listObj.size(); ++i) {
            lazyList.add(this.toLazyObject(listObj.get(i), listElementOI));
        }
        return retList;
    }
    
    private Object toLazyMapObject(final Object obj, final ObjectInspector objectInspector) {
        if (obj == null) {
            return null;
        }
        final LazyMap lazyMap = (LazyMap)LazyFactory.createLazyObject(objectInspector);
        final Map map = lazyMap.getMap();
        final Map<Object, Object> origMap = (Map<Object, Object>)obj;
        final ObjectInspector keyObjectInspector = ((MapObjectInspector)objectInspector).getMapKeyObjectInspector();
        final ObjectInspector valueObjectInspector = ((MapObjectInspector)objectInspector).getMapValueObjectInspector();
        for (final Map.Entry entry : origMap.entrySet()) {
            final Object value = entry.getValue();
            map.put(this.toLazyPrimitiveObject(entry.getKey(), keyObjectInspector), this.toLazyObject(value, valueObjectInspector));
        }
        return lazyMap;
    }
    
    private Object toLazyUnionObject(final Object obj, final ObjectInspector objectInspector) {
        if (obj == null) {
            return null;
        }
        if (!(objectInspector instanceof LazyUnionObjectInspector)) {
            throw new IllegalArgumentException("Invalid objectinspector found. Expected LazyUnionObjectInspector, Found " + objectInspector.getClass());
        }
        final StandardUnionObjectInspector.StandardUnion standardUnion = (StandardUnionObjectInspector.StandardUnion)obj;
        final LazyUnionObjectInspector lazyUnionOI = (LazyUnionObjectInspector)objectInspector;
        final byte tag = standardUnion.getTag();
        final Object field = standardUnion.getObject();
        final ObjectInspector fieldOI = lazyUnionOI.getObjectInspectors().get(tag);
        Object convertedObj = null;
        if (field != null) {
            convertedObj = this.toLazyObject(field, fieldOI);
        }
        if (convertedObj == null) {
            return null;
        }
        return new LazyUnion(lazyUnionOI, tag, convertedObj);
    }
    
    private boolean isPrimitive(final Class<?> clazz) {
        return clazz.isPrimitive() || ClassUtils.wrapperToPrimitive(clazz) != null || clazz.getSimpleName().equals("String");
    }
    
    static {
        LOG = LogFactory.getLog(AvroLazyObjectInspector.class);
    }
}
