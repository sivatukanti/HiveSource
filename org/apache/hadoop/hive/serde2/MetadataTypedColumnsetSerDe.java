// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.objectinspector.MetadataListStructObjectInspector;
import java.util.Arrays;
import java.lang.reflect.Type;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.commons.logging.Log;

@SerDeSpec(schemaProps = { "serialization.format", "serialization.null.format", "serialization.lib", "serialization.last.column.takes.rest" })
public class MetadataTypedColumnsetSerDe extends AbstractSerDe
{
    public static final Log LOG;
    public static final String DefaultSeparator = "\u0001";
    private String separator;
    public static final String defaultNullString = "\\N";
    private String nullString;
    private List<String> columnNames;
    private ObjectInspector cachedObjectInspector;
    private boolean lastColumnTakesRest;
    private int splitLimit;
    ColumnSet deserializeCache;
    Text serializeCache;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    @Override
    public String toString() {
        return "MetaDataTypedColumnsetSerDe[" + this.separator + "," + this.columnNames + "]";
    }
    
    public MetadataTypedColumnsetSerDe() throws SerDeException {
        this.lastColumnTakesRest = false;
        this.splitLimit = -1;
        this.deserializeCache = new ColumnSet();
        this.serializeCache = new Text();
        this.separator = "\u0001";
    }
    
    private String getByteValue(final String altValue, final String defaultVal) {
        if (altValue != null && altValue.length() > 0) {
            try {
                final byte[] b = { Byte.valueOf(altValue) };
                return new String(b);
            }
            catch (NumberFormatException e) {
                return altValue;
            }
        }
        return defaultVal;
    }
    
    @Override
    public void initialize(final Configuration job, final Properties tbl) throws SerDeException {
        final String altSep = tbl.getProperty("serialization.format");
        this.separator = this.getByteValue(altSep, "\u0001");
        final String altNull = tbl.getProperty("serialization.null.format");
        this.nullString = this.getByteValue(altNull, "\\N");
        final String columnProperty = tbl.getProperty("columns");
        final String serdeName = tbl.getProperty("serialization.lib");
        boolean columnsetSerDe = false;
        if (serdeName != null && serdeName.equals("org.apache.hadoop.hive.serde.thrift.columnsetSerDe")) {
            columnsetSerDe = true;
        }
        if (columnProperty == null || columnProperty.length() == 0 || columnsetSerDe) {
            this.cachedObjectInspector = ObjectInspectorFactory.getReflectionObjectInspector(ColumnSet.class, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
        }
        else {
            this.columnNames = Arrays.asList(columnProperty.split(","));
            this.cachedObjectInspector = MetadataListStructObjectInspector.getInstance(this.columnNames);
        }
        final String lastColumnTakesRestString = tbl.getProperty("serialization.last.column.takes.rest");
        this.lastColumnTakesRest = (lastColumnTakesRestString != null && lastColumnTakesRestString.equalsIgnoreCase("true"));
        this.splitLimit = ((this.lastColumnTakesRest && this.columnNames != null) ? this.columnNames.size() : -1);
        MetadataTypedColumnsetSerDe.LOG.debug(this.getClass().getName() + ": initialized with columnNames: " + this.columnNames + " and separator code=" + (int)this.separator.charAt(0) + " lastColumnTakesRest=" + this.lastColumnTakesRest + " splitLimit=" + this.splitLimit);
    }
    
    public static Object deserialize(final ColumnSet c, final String row, final String sep, final String nullString, final int limit) throws Exception {
        if (c.col == null) {
            c.col = new ArrayList<String>();
        }
        else {
            c.col.clear();
        }
        final String[] split;
        final String[] l1 = split = row.split(sep, limit);
        for (final String s : split) {
            if (s.equals(nullString)) {
                c.col.add(null);
            }
            else {
                c.col.add(s);
            }
        }
        return c;
    }
    
    @Override
    public Object deserialize(final Writable field) throws SerDeException {
        String row = null;
        if (field instanceof BytesWritable) {
            final BytesWritable b = (BytesWritable)field;
            try {
                row = Text.decode(b.getBytes(), 0, b.getLength());
            }
            catch (CharacterCodingException e) {
                throw new SerDeException(e);
            }
        }
        else if (field instanceof Text) {
            row = field.toString();
        }
        try {
            deserialize(this.deserializeCache, row, this.separator, this.nullString, this.splitLimit);
            if (this.columnNames != null && !MetadataTypedColumnsetSerDe.$assertionsDisabled && this.columnNames.size() != this.deserializeCache.col.size()) {
                throw new AssertionError();
            }
            return this.deserializeCache;
        }
        catch (ClassCastException e2) {
            throw new SerDeException(this.getClass().getName() + " expects Text or BytesWritable", e2);
        }
        catch (Exception e3) {
            throw new SerDeException(e3);
        }
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.cachedObjectInspector;
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        if (objInspector.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException(this.getClass().toString() + " can only serialize struct types, but we got: " + objInspector.getTypeName());
        }
        final StructObjectInspector soi = (StructObjectInspector)objInspector;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); ++i) {
            if (i > 0) {
                sb.append(this.separator);
            }
            final Object column = soi.getStructFieldData(obj, (StructField)fields.get(i));
            if (((StructField)fields.get(i)).getFieldObjectInspector().getCategory() == ObjectInspector.Category.PRIMITIVE) {
                sb.append((column == null) ? this.nullString : column.toString());
            }
            else {
                sb.append(SerDeUtils.getJSONString(column, ((StructField)fields.get(i)).getFieldObjectInspector()));
            }
        }
        this.serializeCache.set(sb.toString());
        return this.serializeCache;
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(MetadataTypedColumnsetSerDe.class.getName());
    }
}
