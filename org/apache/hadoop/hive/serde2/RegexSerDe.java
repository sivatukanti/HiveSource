// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.commons.logging.LogFactory;
import java.util.regex.Matcher;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.AbstractPrimitiveJavaObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import java.util.Arrays;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;

@SerDeSpec(schemaProps = { "columns", "columns.types", "input.regex", "input.regex.case.insensitive" })
public class RegexSerDe extends AbstractSerDe
{
    public static final Log LOG;
    public static final String INPUT_REGEX = "input.regex";
    public static final String INPUT_REGEX_CASE_SENSITIVE = "input.regex.case.insensitive";
    int numColumns;
    String inputRegex;
    Pattern inputPattern;
    StructObjectInspector rowOI;
    List<Object> row;
    List<TypeInfo> columnTypes;
    Object[] outputFields;
    Text outputRowText;
    boolean alreadyLoggedNoMatch;
    boolean alreadyLoggedPartialMatch;
    long unmatchedRowsCount;
    long partialMatchedRowsCount;
    
    public RegexSerDe() {
        this.alreadyLoggedNoMatch = false;
        this.alreadyLoggedPartialMatch = false;
        this.unmatchedRowsCount = 0L;
        this.partialMatchedRowsCount = 0L;
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
        this.inputRegex = tbl.getProperty("input.regex");
        final String columnNameProperty = tbl.getProperty("columns");
        final String columnTypeProperty = tbl.getProperty("columns.types");
        final boolean inputRegexIgnoreCase = "true".equalsIgnoreCase(tbl.getProperty("input.regex.case.insensitive"));
        if (null != tbl.getProperty("output.format.string")) {
            RegexSerDe.LOG.warn("output.format.string has been deprecated");
        }
        if (this.inputRegex == null) {
            this.inputPattern = null;
            throw new SerDeException("This table does not have serde property \"input.regex\"!");
        }
        this.inputPattern = Pattern.compile(this.inputRegex, 32 + (inputRegexIgnoreCase ? 2 : 0));
        final List<String> columnNames = Arrays.asList(columnNameProperty.split(","));
        this.columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(columnTypeProperty);
        assert columnNames.size() == this.columnTypes.size();
        this.numColumns = columnNames.size();
        final List<ObjectInspector> columnOIs = new ArrayList<ObjectInspector>(columnNames.size());
        for (int c = 0; c < this.numColumns; ++c) {
            final TypeInfo typeInfo = this.columnTypes.get(c);
            if (!(typeInfo instanceof PrimitiveTypeInfo)) {
                throw new SerDeException(this.getClass().getName() + " doesn't allow column [" + c + "] named " + columnNames.get(c) + " with type " + this.columnTypes.get(c));
            }
            final PrimitiveTypeInfo pti = this.columnTypes.get(c);
            final AbstractPrimitiveJavaObjectInspector oi = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(pti);
            columnOIs.add(oi);
        }
        this.rowOI = ObjectInspectorFactory.getStandardStructObjectInspector(columnNames, columnOIs, (List<String>)Lists.newArrayList((Iterable<?>)Splitter.on('\0').split(tbl.getProperty("columns.comments"))));
        this.row = new ArrayList<Object>(this.numColumns);
        for (int c = 0; c < this.numColumns; ++c) {
            this.row.add(null);
        }
        this.outputFields = new Object[this.numColumns];
        this.outputRowText = new Text();
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.rowOI;
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }
    
    @Override
    public Object deserialize(final Writable blob) throws SerDeException {
        final Text rowText = (Text)blob;
        final Matcher m = this.inputPattern.matcher(rowText.toString());
        if (m.groupCount() != this.numColumns) {
            throw new SerDeException("Number of matching groups doesn't match the number of columns");
        }
        if (!m.matches()) {
            ++this.unmatchedRowsCount;
            if (!this.alreadyLoggedNoMatch) {
                RegexSerDe.LOG.warn("" + this.unmatchedRowsCount + " unmatched rows are found: " + rowText);
                this.alreadyLoggedNoMatch = true;
            }
            return null;
        }
        for (int c = 0; c < this.numColumns; ++c) {
            try {
                final String t = m.group(c + 1);
                final TypeInfo typeInfo = this.columnTypes.get(c);
                final PrimitiveTypeInfo pti = (PrimitiveTypeInfo)typeInfo;
                switch (pti.getPrimitiveCategory()) {
                    case STRING: {
                        this.row.set(c, t);
                        break;
                    }
                    case BYTE: {
                        final Byte b = Byte.valueOf(t);
                        this.row.set(c, b);
                        break;
                    }
                    case SHORT: {
                        final Short s = Short.valueOf(t);
                        this.row.set(c, s);
                        break;
                    }
                    case INT: {
                        final Integer i = Integer.valueOf(t);
                        this.row.set(c, i);
                        break;
                    }
                    case LONG: {
                        final Long l = Long.valueOf(t);
                        this.row.set(c, l);
                        break;
                    }
                    case FLOAT: {
                        final Float f = Float.valueOf(t);
                        this.row.set(c, f);
                        break;
                    }
                    case DOUBLE: {
                        final Double d = Double.valueOf(t);
                        this.row.set(c, d);
                        break;
                    }
                    case BOOLEAN: {
                        final Boolean bool = Boolean.valueOf(t);
                        this.row.set(c, bool);
                        break;
                    }
                    case TIMESTAMP: {
                        final Timestamp ts = Timestamp.valueOf(t);
                        this.row.set(c, ts);
                        break;
                    }
                    case DATE: {
                        final Date date = Date.valueOf(t);
                        this.row.set(c, date);
                        break;
                    }
                    case DECIMAL: {
                        final HiveDecimal bd = HiveDecimal.create(t);
                        this.row.set(c, bd);
                        break;
                    }
                    case CHAR: {
                        final HiveChar hc = new HiveChar(t, ((CharTypeInfo)typeInfo).getLength());
                        this.row.set(c, hc);
                        break;
                    }
                    case VARCHAR: {
                        final HiveVarchar hv = new HiveVarchar(t, ((VarcharTypeInfo)typeInfo).getLength());
                        this.row.set(c, hv);
                        break;
                    }
                    default: {
                        throw new SerDeException("Unsupported type " + typeInfo);
                    }
                }
            }
            catch (RuntimeException e) {
                ++this.partialMatchedRowsCount;
                if (!this.alreadyLoggedPartialMatch) {
                    RegexSerDe.LOG.warn("" + this.partialMatchedRowsCount + " partially unmatched rows are found, " + " cannot find group " + c + ": " + rowText);
                    this.alreadyLoggedPartialMatch = true;
                }
                this.row.set(c, null);
            }
        }
        return this.row;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        throw new UnsupportedOperationException("Regex SerDe doesn't support the serialize() method");
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(RegexSerDe.class.getName());
    }
}
