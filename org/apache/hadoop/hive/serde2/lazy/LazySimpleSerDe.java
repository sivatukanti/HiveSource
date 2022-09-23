// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import java.util.Iterator;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import java.io.OutputStream;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParametersImpl;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeSpec;
import org.apache.hadoop.hive.serde2.AbstractEncodingAwareSerDe;

@SerDeSpec(schemaProps = { "columns", "columns.types", "field.delim", "colelction.delim", "mapkey.delim", "serialization.format", "serialization.null.format", "serialization.last.column.takes.rest", "escape.delim", "serialization.encoding", "hive.serialization.extend.nesting.levels", "hive.serialization.extend.additional.nesting.levels" })
public class LazySimpleSerDe extends AbstractEncodingAwareSerDe
{
    public static final Log LOG;
    private LazySerDeParameters serdeParams;
    private ObjectInspector cachedObjectInspector;
    private long serializedSize;
    private SerDeStats stats;
    private boolean lastOperationSerialize;
    private boolean lastOperationDeserialize;
    LazyStruct cachedLazyStruct;
    ByteArrayRef byteArrayRef;
    Text serializeCache;
    ByteStream.Output serializeStream;
    
    @Override
    public String toString() {
        return this.getClass().toString() + "[" + Arrays.asList(new byte[][] { this.serdeParams.getSeparators() }) + ":" + ((StructTypeInfo)this.serdeParams.getRowTypeInfo()).getAllStructFieldNames() + ":" + ((StructTypeInfo)this.serdeParams.getRowTypeInfo()).getAllStructFieldTypeInfos() + "]";
    }
    
    public LazySimpleSerDe() throws SerDeException {
        this.serdeParams = null;
        this.serializeCache = new Text();
        this.serializeStream = new ByteStream.Output();
    }
    
    @Override
    public void initialize(final Configuration job, final Properties tbl) throws SerDeException {
        super.initialize(job, tbl);
        this.serdeParams = new LazySerDeParameters(job, tbl, this.getClass().getName());
        this.cachedObjectInspector = LazyFactory.createLazyStructInspector(this.serdeParams.getColumnNames(), this.serdeParams.getColumnTypes(), new LazyObjectInspectorParametersImpl(this.serdeParams));
        this.cachedLazyStruct = (LazyStruct)LazyFactory.createLazyObject(this.cachedObjectInspector);
        LazySimpleSerDe.LOG.debug(this.getClass().getName() + " initialized with: columnNames=" + this.serdeParams.getColumnNames() + " columnTypes=" + this.serdeParams.getColumnTypes() + " separator=" + Arrays.asList(new byte[][] { this.serdeParams.getSeparators() }) + " nullstring=" + this.serdeParams.getNullString() + " lastColumnTakesRest=" + this.serdeParams.isLastColumnTakesRest() + " timestampFormats=" + this.serdeParams.getTimestampFormats());
        this.serializedSize = 0L;
        this.stats = new SerDeStats();
        this.lastOperationSerialize = false;
        this.lastOperationDeserialize = false;
    }
    
    public Object doDeserialize(final Writable field) throws SerDeException {
        if (this.byteArrayRef == null) {
            this.byteArrayRef = new ByteArrayRef();
        }
        final BinaryComparable b = (BinaryComparable)field;
        this.byteArrayRef.setData(b.getBytes());
        this.cachedLazyStruct.init(this.byteArrayRef, 0, b.getLength());
        this.lastOperationSerialize = false;
        this.lastOperationDeserialize = true;
        return this.cachedLazyStruct;
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.cachedObjectInspector;
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }
    
    public Writable doSerialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        if (objInspector.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException(this.getClass().toString() + " can only serialize struct types, but we got: " + objInspector.getTypeName());
        }
        final StructObjectInspector soi = (StructObjectInspector)objInspector;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final List<Object> list = soi.getStructFieldsDataAsList(obj);
        final List<? extends StructField> declaredFields = (this.serdeParams.getRowTypeInfo() != null && ((StructTypeInfo)this.serdeParams.getRowTypeInfo()).getAllStructFieldNames().size() > 0) ? ((StructObjectInspector)this.getObjectInspector()).getAllStructFieldRefs() : null;
        this.serializeStream.reset();
        this.serializedSize = 0L;
        for (int i = 0; i < fields.size(); ++i) {
            if (i > 0) {
                this.serializeStream.write(this.serdeParams.getSeparators()[0]);
            }
            final ObjectInspector foi = ((StructField)fields.get(i)).getFieldObjectInspector();
            final Object f = (list == null) ? null : list.get(i);
            if (declaredFields != null && i >= declaredFields.size()) {
                throw new SerDeException("Error: expecting " + declaredFields.size() + " but asking for field " + i + "\n" + "data=" + obj + "\n" + "tableType=" + this.serdeParams.getRowTypeInfo().toString() + "\n" + "dataType=" + TypeInfoUtils.getTypeInfoFromObjectInspector(objInspector));
            }
            this.serializeField(this.serializeStream, f, foi, this.serdeParams);
        }
        this.serializeCache.set(this.serializeStream.getData(), 0, this.serializeStream.getLength());
        this.serializedSize = this.serializeStream.getLength();
        this.lastOperationSerialize = true;
        this.lastOperationDeserialize = false;
        return this.serializeCache;
    }
    
    protected void serializeField(final ByteStream.Output out, final Object obj, final ObjectInspector objInspector, final LazySerDeParameters serdeParams) throws SerDeException {
        try {
            serialize(out, obj, objInspector, serdeParams.getSeparators(), 1, serdeParams.getNullSequence(), serdeParams.isEscaped(), serdeParams.getEscapeChar(), serdeParams.getNeedsEscape());
        }
        catch (IOException e) {
            throw new SerDeException(e);
        }
    }
    
    public static void serialize(final ByteStream.Output out, final Object obj, final ObjectInspector objInspector, final byte[] separators, final int level, final Text nullSequence, final boolean escaped, final byte escapeChar, final boolean[] needsEscape) throws IOException, SerDeException {
        if (obj == null) {
            out.write(nullSequence.getBytes(), 0, nullSequence.getLength());
            return;
        }
        switch (objInspector.getCategory()) {
            case PRIMITIVE: {
                LazyUtils.writePrimitiveUTF8(out, obj, (PrimitiveObjectInspector)objInspector, escaped, escapeChar, needsEscape);
            }
            case LIST: {
                final char separator = (char)LazyUtils.getSeparator(separators, level);
                final ListObjectInspector loi = (ListObjectInspector)objInspector;
                final List<?> list = loi.getList(obj);
                final ObjectInspector eoi = loi.getListElementObjectInspector();
                if (list == null) {
                    out.write(nullSequence.getBytes(), 0, nullSequence.getLength());
                }
                else {
                    for (int i = 0; i < list.size(); ++i) {
                        if (i > 0) {
                            out.write(separator);
                        }
                        serialize(out, list.get(i), eoi, separators, level + 1, nullSequence, escaped, escapeChar, needsEscape);
                    }
                }
            }
            case MAP: {
                final char separator = (char)LazyUtils.getSeparator(separators, level);
                final char keyValueSeparator = (char)LazyUtils.getSeparator(separators, level + 1);
                final MapObjectInspector moi = (MapObjectInspector)objInspector;
                final ObjectInspector koi = moi.getMapKeyObjectInspector();
                final ObjectInspector voi = moi.getMapValueObjectInspector();
                final Map<?, ?> map = moi.getMap(obj);
                if (map == null) {
                    out.write(nullSequence.getBytes(), 0, nullSequence.getLength());
                }
                else {
                    boolean first = true;
                    for (final Map.Entry<?, ?> entry : map.entrySet()) {
                        if (first) {
                            first = false;
                        }
                        else {
                            out.write(separator);
                        }
                        serialize(out, entry.getKey(), koi, separators, level + 2, nullSequence, escaped, escapeChar, needsEscape);
                        out.write(keyValueSeparator);
                        serialize(out, entry.getValue(), voi, separators, level + 2, nullSequence, escaped, escapeChar, needsEscape);
                    }
                }
            }
            case STRUCT: {
                final char separator = (char)LazyUtils.getSeparator(separators, level);
                final StructObjectInspector soi = (StructObjectInspector)objInspector;
                final List<? extends StructField> fields = soi.getAllStructFieldRefs();
                final List<?> list = soi.getStructFieldsDataAsList(obj);
                if (list == null) {
                    out.write(nullSequence.getBytes(), 0, nullSequence.getLength());
                }
                else {
                    for (int j = 0; j < list.size(); ++j) {
                        if (j > 0) {
                            out.write(separator);
                        }
                        serialize(out, list.get(j), ((StructField)fields.get(j)).getFieldObjectInspector(), separators, level + 1, nullSequence, escaped, escapeChar, needsEscape);
                    }
                }
            }
            case UNION: {
                final char separator = (char)LazyUtils.getSeparator(separators, level);
                final UnionObjectInspector uoi = (UnionObjectInspector)objInspector;
                final List<? extends ObjectInspector> ois = uoi.getObjectInspectors();
                if (ois == null) {
                    out.write(nullSequence.getBytes(), 0, nullSequence.getLength());
                }
                else {
                    LazyUtils.writePrimitiveUTF8(out, new Byte(uoi.getTag(obj)), PrimitiveObjectInspectorFactory.javaByteObjectInspector, escaped, escapeChar, needsEscape);
                    out.write(separator);
                    serialize(out, uoi.getField(obj), (ObjectInspector)ois.get(uoi.getTag(obj)), separators, level + 1, nullSequence, escaped, escapeChar, needsEscape);
                }
            }
            default: {
                throw new RuntimeException("Unknown category type: " + objInspector.getCategory());
            }
        }
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        assert this.lastOperationSerialize != this.lastOperationDeserialize;
        if (this.lastOperationSerialize) {
            this.stats.setRawDataSize(this.serializedSize);
        }
        else {
            this.stats.setRawDataSize(this.cachedLazyStruct.getRawDataSerializedSize());
        }
        return this.stats;
    }
    
    @Override
    protected Writable transformFromUTF8(final Writable blob) {
        final Text text = (Text)blob;
        return SerDeUtils.transformTextFromUTF8(text, this.charset);
    }
    
    @Override
    protected Writable transformToUTF8(final Writable blob) {
        final Text text = (Text)blob;
        return SerDeUtils.transformTextToUTF8(text, this.charset);
    }
    
    static {
        LOG = LogFactory.getLog(LazySimpleSerDe.class.getName());
    }
}
