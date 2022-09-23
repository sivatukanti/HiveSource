// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import java.io.IOException;
import org.apache.avro.io.Decoder;
import java.io.InputStream;
import org.apache.avro.io.DecoderFactory;
import java.io.ByteArrayInputStream;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.BinaryEncoder;
import java.io.OutputStream;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.generic.GenericDatumWriter;
import java.io.ByteArrayOutputStream;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.util.Iterator;
import org.apache.hadoop.hive.serde2.objectinspector.StandardUnionObjectInspector;
import java.util.Set;
import org.apache.avro.UnresolvedUnionException;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaHiveDecimalObjectInspector;
import java.nio.ByteBuffer;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import java.util.ArrayList;
import org.apache.avro.Schema;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import java.util.HashMap;
import java.rmi.server.UID;
import java.util.HashSet;
import org.apache.commons.logging.Log;

class AvroDeserializer
{
    private static final Log LOG;
    private final HashSet<UID> noEncodingNeeded;
    private final HashMap<UID, SchemaReEncoder> reEncoderCache;
    private static boolean warnedOnce;
    private List<Object> row;
    
    AvroDeserializer() {
        this.noEncodingNeeded = new HashSet<UID>();
        this.reEncoderCache = new HashMap<UID, SchemaReEncoder>();
    }
    
    public Object deserialize(final List<String> columnNames, final List<TypeInfo> columnTypes, final Writable writable, final Schema readerSchema) throws AvroSerdeException {
        if (!(writable instanceof AvroGenericRecordWritable)) {
            throw new AvroSerdeException("Expecting a AvroGenericRecordWritable");
        }
        if (this.row == null || this.row.size() != columnNames.size()) {
            this.row = new ArrayList<Object>(columnNames.size());
        }
        else {
            this.row.clear();
        }
        final AvroGenericRecordWritable recordWritable = (AvroGenericRecordWritable)writable;
        GenericRecord r = recordWritable.getRecord();
        final Schema fileSchema = recordWritable.getFileSchema();
        final UID recordReaderId = recordWritable.getRecordReaderID();
        if (!this.noEncodingNeeded.contains(recordReaderId)) {
            SchemaReEncoder reEncoder = null;
            if (this.reEncoderCache.containsKey(recordReaderId)) {
                reEncoder = this.reEncoderCache.get(recordReaderId);
            }
            else if (!r.getSchema().equals(readerSchema)) {
                reEncoder = new SchemaReEncoder(r.getSchema(), readerSchema);
                this.reEncoderCache.put(recordReaderId, reEncoder);
            }
            else {
                AvroDeserializer.LOG.info("Adding new valid RRID :" + recordReaderId);
                this.noEncodingNeeded.add(recordReaderId);
            }
            if (reEncoder != null) {
                if (!AvroDeserializer.warnedOnce) {
                    AvroDeserializer.LOG.warn("Received different schemas.  Have to re-encode: " + r.getSchema().toString(false) + "\nSIZE" + this.reEncoderCache + " ID " + recordReaderId);
                    AvroDeserializer.warnedOnce = true;
                }
                r = reEncoder.reencode(r);
            }
        }
        this.workerBase(this.row, fileSchema, columnNames, columnTypes, r);
        return this.row;
    }
    
    private List<Object> workerBase(final List<Object> objectRow, final Schema fileSchema, final List<String> columnNames, final List<TypeInfo> columnTypes, final GenericRecord record) throws AvroSerdeException {
        for (int i = 0; i < columnNames.size(); ++i) {
            final TypeInfo columnType = columnTypes.get(i);
            final String columnName = columnNames.get(i);
            final Object datum = record.get(columnName);
            final Schema datumSchema = record.getSchema().getField(columnName).schema();
            final Schema.Field field = AvroSerdeUtils.isNullableType(fileSchema) ? AvroSerdeUtils.getOtherTypeFromNullableType(fileSchema).getField(columnName) : fileSchema.getField(columnName);
            objectRow.add(this.worker(datum, (field == null) ? null : field.schema(), datumSchema, columnType));
        }
        return objectRow;
    }
    
    private Object worker(final Object datum, final Schema fileSchema, final Schema recordSchema, final TypeInfo columnType) throws AvroSerdeException {
        if (AvroSerdeUtils.isNullableType(recordSchema)) {
            return this.deserializeNullableUnion(datum, fileSchema, recordSchema);
        }
        switch (columnType.getCategory()) {
            case STRUCT: {
                return this.deserializeStruct((GenericData.Record)datum, fileSchema, (StructTypeInfo)columnType);
            }
            case UNION: {
                return this.deserializeUnion(datum, fileSchema, recordSchema, (UnionTypeInfo)columnType);
            }
            case LIST: {
                return this.deserializeList(datum, fileSchema, recordSchema, (ListTypeInfo)columnType);
            }
            case MAP: {
                return this.deserializeMap(datum, fileSchema, recordSchema, (MapTypeInfo)columnType);
            }
            case PRIMITIVE: {
                return this.deserializePrimitive(datum, fileSchema, recordSchema, (PrimitiveTypeInfo)columnType);
            }
            default: {
                throw new AvroSerdeException("Unknown TypeInfo: " + columnType.getCategory());
            }
        }
    }
    
    private Object deserializePrimitive(final Object datum, final Schema fileSchema, final Schema recordSchema, final PrimitiveTypeInfo columnType) throws AvroSerdeException {
        switch (columnType.getPrimitiveCategory()) {
            case STRING: {
                return datum.toString();
            }
            case BINARY: {
                if (recordSchema.getType() == Schema.Type.FIXED) {
                    final GenericData.Fixed fixed = (GenericData.Fixed)datum;
                    return fixed.bytes();
                }
                if (recordSchema.getType() == Schema.Type.BYTES) {
                    return AvroSerdeUtils.getBytesFromByteBuffer((ByteBuffer)datum);
                }
                throw new AvroSerdeException("Unexpected Avro schema for Binary TypeInfo: " + recordSchema.getType());
            }
            case DECIMAL: {
                if (fileSchema == null) {
                    throw new AvroSerdeException("File schema is missing for decimal field. Reader schema is " + columnType);
                }
                int scale = 0;
                try {
                    scale = fileSchema.getJsonProp("scale").getIntValue();
                }
                catch (Exception ex) {
                    throw new AvroSerdeException("Failed to obtain scale value from file schema: " + fileSchema, ex);
                }
                final HiveDecimal dec = AvroSerdeUtils.getHiveDecimalFromByteBuffer((ByteBuffer)datum, scale);
                final JavaHiveDecimalObjectInspector oi = (JavaHiveDecimalObjectInspector)PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(columnType);
                return oi.set(null, dec);
            }
            case CHAR: {
                if (fileSchema == null) {
                    throw new AvroSerdeException("File schema is missing for char field. Reader schema is " + columnType);
                }
                int maxLength = 0;
                try {
                    maxLength = fileSchema.getJsonProp("maxLength").getValueAsInt();
                }
                catch (Exception ex2) {
                    throw new AvroSerdeException("Failed to obtain maxLength value for char field from file schema: " + fileSchema, ex2);
                }
                final String str = datum.toString();
                final HiveChar hc = new HiveChar(str, maxLength);
                return hc;
            }
            case VARCHAR: {
                if (fileSchema == null) {
                    throw new AvroSerdeException("File schema is missing for varchar field. Reader schema is " + columnType);
                }
                int maxLength = 0;
                try {
                    maxLength = fileSchema.getJsonProp("maxLength").getValueAsInt();
                }
                catch (Exception ex3) {
                    throw new AvroSerdeException("Failed to obtain maxLength value for varchar field from file schema: " + fileSchema, ex3);
                }
                final String str = datum.toString();
                final HiveVarchar hvc = new HiveVarchar(str, maxLength);
                return hvc;
            }
            case DATE: {
                if (recordSchema.getType() != Schema.Type.INT) {
                    throw new AvroSerdeException("Unexpected Avro schema for Date TypeInfo: " + recordSchema.getType());
                }
                return new Date(DateWritable.daysToMillis((int)datum));
            }
            case TIMESTAMP: {
                if (recordSchema.getType() != Schema.Type.LONG) {
                    throw new AvroSerdeException("Unexpected Avro schema for Date TypeInfo: " + recordSchema.getType());
                }
                return new Timestamp((long)datum);
            }
            default: {
                return datum;
            }
        }
    }
    
    private Object deserializeNullableUnion(final Object datum, final Schema fileSchema, final Schema recordSchema) throws AvroSerdeException {
        int tag = GenericData.get().resolveUnion(recordSchema, datum);
        final Schema schema = recordSchema.getTypes().get(tag);
        if (schema.getType().equals(Schema.Type.NULL)) {
            return null;
        }
        Schema currentFileSchema = null;
        if (fileSchema != null) {
            if (fileSchema.getType() == Schema.Type.UNION) {
                try {
                    tag = GenericData.get().resolveUnion(fileSchema, datum);
                    currentFileSchema = fileSchema.getTypes().get(tag);
                }
                catch (UnresolvedUnionException e) {
                    if (AvroDeserializer.LOG.isDebugEnabled()) {
                        String datumClazz = null;
                        if (datum != null) {
                            datumClazz = datum.getClass().getName();
                        }
                        final String msg = "File schema union could not resolve union. fileSchema = " + fileSchema + ", recordSchema = " + recordSchema + ", datum class = " + datumClazz + ": " + e;
                        AvroDeserializer.LOG.debug(msg, e);
                    }
                    currentFileSchema = schema;
                }
            }
            else {
                currentFileSchema = fileSchema;
            }
        }
        return this.worker(datum, currentFileSchema, schema, SchemaToTypeInfo.generateTypeInfo(schema, null));
    }
    
    private Object deserializeStruct(final GenericData.Record datum, final Schema fileSchema, final StructTypeInfo columnType) throws AvroSerdeException {
        final ArrayList<TypeInfo> innerFieldTypes = columnType.getAllStructFieldTypeInfos();
        final ArrayList<String> innerFieldNames = columnType.getAllStructFieldNames();
        final List<Object> innerObjectRow = new ArrayList<Object>(innerFieldTypes.size());
        return this.workerBase(innerObjectRow, fileSchema, innerFieldNames, innerFieldTypes, datum);
    }
    
    private Object deserializeUnion(final Object datum, final Schema fileSchema, final Schema recordSchema, final UnionTypeInfo columnType) throws AvroSerdeException {
        final int tag = GenericData.get().resolveUnion(recordSchema, datum);
        final Object desered = this.worker(datum, (fileSchema == null) ? null : fileSchema.getTypes().get(tag), recordSchema.getTypes().get(tag), columnType.getAllUnionObjectTypeInfos().get(tag));
        return new StandardUnionObjectInspector.StandardUnion((byte)tag, desered);
    }
    
    private Object deserializeList(final Object datum, final Schema fileSchema, final Schema recordSchema, final ListTypeInfo columnType) throws AvroSerdeException {
        if (recordSchema.getType().equals(Schema.Type.FIXED)) {
            final GenericData.Fixed fixed = (GenericData.Fixed)datum;
            final List<Byte> asList = new ArrayList<Byte>(fixed.bytes().length);
            for (int j = 0; j < fixed.bytes().length; ++j) {
                asList.add(fixed.bytes()[j]);
            }
            return asList;
        }
        if (recordSchema.getType().equals(Schema.Type.BYTES)) {
            final ByteBuffer bb = (ByteBuffer)datum;
            final List<Byte> asList = new ArrayList<Byte>(bb.capacity());
            final byte[] array = bb.array();
            for (int i = 0; i < array.length; ++i) {
                asList.add(array[i]);
            }
            return asList;
        }
        final List listData = (List)datum;
        final Schema listSchema = recordSchema.getElementType();
        final List<Object> listContents = new ArrayList<Object>(listData.size());
        for (final Object obj : listData) {
            listContents.add(this.worker(obj, (fileSchema == null) ? null : fileSchema.getElementType(), listSchema, columnType.getListElementTypeInfo()));
        }
        return listContents;
    }
    
    private Object deserializeMap(final Object datum, final Schema fileSchema, final Schema mapSchema, final MapTypeInfo columnType) throws AvroSerdeException {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Map<CharSequence, Object> mapDatum = (Map<CharSequence, Object>)datum;
        final Schema valueSchema = mapSchema.getValueType();
        final TypeInfo valueTypeInfo = columnType.getMapValueTypeInfo();
        for (final CharSequence key : mapDatum.keySet()) {
            final Object value = mapDatum.get(key);
            map.put(key.toString(), this.worker(value, (fileSchema == null) ? null : fileSchema.getValueType(), valueSchema, valueTypeInfo));
        }
        return map;
    }
    
    public HashSet<UID> getNoEncodingNeeded() {
        return this.noEncodingNeeded;
    }
    
    public HashMap<UID, SchemaReEncoder> getReEncoderCache() {
        return this.reEncoderCache;
    }
    
    static {
        LOG = LogFactory.getLog(AvroDeserializer.class);
        AvroDeserializer.warnedOnce = false;
    }
    
    static class SchemaReEncoder
    {
        private final ByteArrayOutputStream baos;
        private final GenericDatumWriter<GenericRecord> gdw;
        private BinaryDecoder binaryDecoder;
        GenericDatumReader<GenericRecord> gdr;
        
        public SchemaReEncoder(final Schema writer, final Schema reader) {
            this.baos = new ByteArrayOutputStream();
            this.gdw = new GenericDatumWriter<GenericRecord>();
            this.binaryDecoder = null;
            this.gdr = null;
            this.gdr = new GenericDatumReader<GenericRecord>(writer, reader);
        }
        
        public GenericRecord reencode(final GenericRecord r) throws AvroSerdeException {
            this.baos.reset();
            final BinaryEncoder be = EncoderFactory.get().directBinaryEncoder(this.baos, null);
            this.gdw.setSchema(r.getSchema());
            try {
                this.gdw.write(r, be);
                final ByteArrayInputStream bais = new ByteArrayInputStream(this.baos.toByteArray());
                this.binaryDecoder = DecoderFactory.defaultFactory().createBinaryDecoder(bais, this.binaryDecoder);
                return this.gdr.read(r, this.binaryDecoder);
            }
            catch (IOException e) {
                throw new AvroSerdeException("Exception trying to re-encode record to new schema", e);
            }
        }
    }
}
