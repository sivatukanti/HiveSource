// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.ColumnProjectionUtils;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazy.LazySerDeParameters;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.hadoop.hive.serde2.SerDeSpec;

@SerDeSpec(schemaProps = { "columns", "columns.types" })
public class LazyBinaryColumnarSerDe extends ColumnarSerDeBase
{
    private List<String> columnNames;
    private List<TypeInfo> columnTypes;
    static final byte[] INVALID_UTF__SINGLE_BYTE;
    
    @Override
    public String toString() {
        return this.getClass().toString() + "[" + this.columnNames + ":" + this.columnTypes + "]";
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
        final LazySerDeParameters serdeParams = new LazySerDeParameters(conf, tbl, this.getClass().getName());
        this.columnNames = serdeParams.getColumnNames();
        this.columnTypes = serdeParams.getColumnTypes();
        this.cachedObjectInspector = LazyBinaryFactory.createColumnarStructInspector(this.columnNames, this.columnTypes);
        final int size = this.columnTypes.size();
        List<Integer> notSkipIDs = new ArrayList<Integer>();
        if (conf == null || ColumnProjectionUtils.isReadAllColumns(conf)) {
            for (int i = 0; i < size; ++i) {
                notSkipIDs.add(i);
            }
        }
        else {
            notSkipIDs = ColumnProjectionUtils.getReadColumnIDs(conf);
        }
        this.cachedLazyStruct = new LazyBinaryColumnarStruct(this.cachedObjectInspector, notSkipIDs);
        super.initialize(size);
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        if (objInspector.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException(this.getClass().toString() + " can only serialize struct types, but we got: " + objInspector.getTypeName());
        }
        final StructObjectInspector soi = (StructObjectInspector)objInspector;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final List<Object> list = soi.getStructFieldsDataAsList(obj);
        final LazyBinarySerDe.BooleanRef warnedOnceNullMapKey = new LazyBinarySerDe.BooleanRef(false);
        this.serializeStream.reset();
        this.serializedSize = 0L;
        int streamOffset = 0;
        for (int i = 0; i < fields.size(); ++i) {
            final ObjectInspector foi = ((StructField)fields.get(i)).getFieldObjectInspector();
            final Object f = (list == null) ? null : list.get(i);
            if (f != null && foi.getCategory().equals(ObjectInspector.Category.PRIMITIVE) && ((PrimitiveObjectInspector)foi).getPrimitiveCategory().equals(PrimitiveObjectInspector.PrimitiveCategory.STRING) && ((StringObjectInspector)foi).getPrimitiveJavaObject(f).length() == 0) {
                this.serializeStream.write(LazyBinaryColumnarSerDe.INVALID_UTF__SINGLE_BYTE, 0, 1);
            }
            else {
                LazyBinarySerDe.serialize(this.serializeStream, f, foi, true, warnedOnceNullMapKey);
            }
            this.field[i].set(this.serializeStream.getData(), streamOffset, this.serializeStream.getLength() - streamOffset);
            streamOffset = this.serializeStream.getLength();
        }
        this.serializedSize = this.serializeStream.getLength();
        this.lastOperationSerialize = true;
        this.lastOperationDeserialize = false;
        return this.serializeCache;
    }
    
    static {
        INVALID_UTF__SINGLE_BYTE = new byte[] { (byte)Integer.parseInt("10111111", 2) };
    }
}
