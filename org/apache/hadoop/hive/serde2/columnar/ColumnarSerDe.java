// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.hadoop.hive.serde2.ColumnProjectionUtils;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.lazy.LazyFactory;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.lazy.LazySerDeParameters;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeSpec;

@SerDeSpec(schemaProps = { "columns", "columns.types", "field.delim", "colelction.delim", "mapkey.delim", "serialization.format", "serialization.null.format", "serialization.last.column.takes.rest", "escape.delim", "serialization.encoding", "hive.serialization.extend.nesting.levels", "hive.serialization.extend.additional.nesting.levels" })
public class ColumnarSerDe extends ColumnarSerDeBase
{
    public static final Log LOG;
    protected LazySerDeParameters serdeParams;
    
    @Override
    public String toString() {
        return this.getClass().toString() + "[" + Arrays.asList(new byte[][] { this.serdeParams.getSeparators() }) + ":" + ((StructTypeInfo)this.serdeParams.getRowTypeInfo()).getAllStructFieldNames() + ":" + ((StructTypeInfo)this.serdeParams.getRowTypeInfo()).getAllStructFieldTypeInfos() + "]";
    }
    
    public ColumnarSerDe() throws SerDeException {
        this.serdeParams = null;
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
        this.serdeParams = new LazySerDeParameters(conf, tbl, this.getClass().getName());
        this.cachedObjectInspector = LazyFactory.createColumnarStructInspector(this.serdeParams.getColumnNames(), this.serdeParams.getColumnTypes(), this.serdeParams);
        final int size = this.serdeParams.getColumnTypes().size();
        List<Integer> notSkipIDs = new ArrayList<Integer>();
        if (conf == null || ColumnProjectionUtils.isReadAllColumns(conf)) {
            for (int i = 0; i < size; ++i) {
                notSkipIDs.add(i);
            }
        }
        else {
            notSkipIDs = ColumnProjectionUtils.getReadColumnIDs(conf);
        }
        this.cachedLazyStruct = new ColumnarStruct(this.cachedObjectInspector, notSkipIDs, this.serdeParams.getNullSequence());
        super.initialize(size);
        ColumnarSerDe.LOG.debug("ColumnarSerDe initialized with: columnNames=" + this.serdeParams.getColumnNames() + " columnTypes=" + this.serdeParams.getColumnTypes() + " separator=" + Arrays.asList(new byte[][] { this.serdeParams.getSeparators() }) + " nullstring=" + this.serdeParams.getNullString());
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        if (objInspector.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException(this.getClass().toString() + " can only serialize struct types, but we got: " + objInspector.getTypeName());
        }
        final StructObjectInspector soi = (StructObjectInspector)objInspector;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final List<Object> list = soi.getStructFieldsDataAsList(obj);
        final List<? extends StructField> declaredFields = (this.serdeParams.getRowTypeInfo() != null && ((StructTypeInfo)this.serdeParams.getRowTypeInfo()).getAllStructFieldNames().size() > 0) ? ((StructObjectInspector)this.getObjectInspector()).getAllStructFieldRefs() : null;
        try {
            this.serializeStream.reset();
            this.serializedSize = 0L;
            int count = 0;
            for (int i = 0; i < fields.size(); ++i) {
                final ObjectInspector foi = ((StructField)fields.get(i)).getFieldObjectInspector();
                final Object f = (list == null) ? null : list.get(i);
                if (declaredFields != null && i >= declaredFields.size()) {
                    throw new SerDeException("Error: expecting " + declaredFields.size() + " but asking for field " + i + "\n" + "data=" + obj + "\n" + "tableType=" + this.serdeParams.getRowTypeInfo().toString() + "\n" + "dataType=" + TypeInfoUtils.getTypeInfoFromObjectInspector(objInspector));
                }
                if (!foi.getCategory().equals(ObjectInspector.Category.PRIMITIVE) && (declaredFields == null || ((StructField)declaredFields.get(i)).getFieldObjectInspector().getCategory().equals(ObjectInspector.Category.PRIMITIVE))) {
                    LazySimpleSerDe.serialize(this.serializeStream, SerDeUtils.getJSONString(f, foi), PrimitiveObjectInspectorFactory.javaStringObjectInspector, this.serdeParams.getSeparators(), 1, this.serdeParams.getNullSequence(), this.serdeParams.isEscaped(), this.serdeParams.getEscapeChar(), this.serdeParams.getNeedsEscape());
                }
                else {
                    LazySimpleSerDe.serialize(this.serializeStream, f, foi, this.serdeParams.getSeparators(), 1, this.serdeParams.getNullSequence(), this.serdeParams.isEscaped(), this.serdeParams.getEscapeChar(), this.serdeParams.getNeedsEscape());
                }
                this.field[i].set(this.serializeStream.getData(), count, this.serializeStream.getLength() - count);
                count = this.serializeStream.getLength();
            }
            this.serializedSize = this.serializeStream.getLength();
            this.lastOperationSerialize = true;
            this.lastOperationDeserialize = false;
        }
        catch (IOException e) {
            throw new SerDeException(e);
        }
        return this.serializeCache;
    }
    
    static {
        LOG = LogFactory.getLog(ColumnarSerDe.class.getName());
    }
}
