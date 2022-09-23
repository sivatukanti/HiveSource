// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.commons.logging.LogFactory;
import au.com.bytecode.opencsv.CSVReader;
import java.io.Reader;
import java.io.CharArrayReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import java.io.Writer;
import java.io.StringWriter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.commons.logging.Log;

@SerDeSpec(schemaProps = { "columns", "separatorChar", "quoteChar", "escapeChar" })
public final class OpenCSVSerde extends AbstractSerDe
{
    public static final Log LOG;
    private ObjectInspector inspector;
    private String[] outputFields;
    private int numCols;
    private List<String> row;
    private char separatorChar;
    private char quoteChar;
    private char escapeChar;
    public static final String SEPARATORCHAR = "separatorChar";
    public static final String QUOTECHAR = "quoteChar";
    public static final String ESCAPECHAR = "escapeChar";
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
        final List<String> columnNames = Arrays.asList(tbl.getProperty("columns").split(","));
        this.numCols = columnNames.size();
        final List<ObjectInspector> columnOIs = new ArrayList<ObjectInspector>(this.numCols);
        for (int i = 0; i < this.numCols; ++i) {
            columnOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        }
        this.inspector = ObjectInspectorFactory.getStandardStructObjectInspector(columnNames, columnOIs);
        this.outputFields = new String[this.numCols];
        this.row = new ArrayList<String>(this.numCols);
        for (int i = 0; i < this.numCols; ++i) {
            this.row.add(null);
        }
        this.separatorChar = this.getProperty(tbl, "separatorChar", ',');
        this.quoteChar = this.getProperty(tbl, "quoteChar", '\"');
        this.escapeChar = this.getProperty(tbl, "escapeChar", '\"');
    }
    
    private char getProperty(final Properties tbl, final String property, final char def) {
        final String val = tbl.getProperty(property);
        if (val != null) {
            return val.charAt(0);
        }
        return def;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        final StructObjectInspector outputRowOI = (StructObjectInspector)objInspector;
        final List<? extends StructField> outputFieldRefs = outputRowOI.getAllStructFieldRefs();
        if (outputFieldRefs.size() != this.numCols) {
            throw new SerDeException("Cannot serialize the object because there are " + outputFieldRefs.size() + " fields but the table has " + this.numCols + " columns.");
        }
        for (int c = 0; c < this.numCols; ++c) {
            final Object field = outputRowOI.getStructFieldData(obj, (StructField)outputFieldRefs.get(c));
            final ObjectInspector fieldOI = ((StructField)outputFieldRefs.get(c)).getFieldObjectInspector();
            final StringObjectInspector fieldStringOI = (StringObjectInspector)fieldOI;
            this.outputFields[c] = fieldStringOI.getPrimitiveJavaObject(field);
        }
        final StringWriter writer = new StringWriter();
        final CSVWriter csv = this.newWriter(writer, this.separatorChar, this.quoteChar, this.escapeChar);
        try {
            csv.writeNext(this.outputFields);
            csv.close();
            return new Text(writer.toString());
        }
        catch (IOException ioe) {
            throw new SerDeException(ioe);
        }
    }
    
    @Override
    public Object deserialize(final Writable blob) throws SerDeException {
        final Text rowText = (Text)blob;
        CSVReader csv = null;
        try {
            csv = this.newReader(new CharArrayReader(rowText.toString().toCharArray()), this.separatorChar, this.quoteChar, this.escapeChar);
            final String[] read = csv.readNext();
            for (int i = 0; i < this.numCols; ++i) {
                if (read != null && i < read.length) {
                    this.row.set(i, read[i]);
                }
                else {
                    this.row.set(i, null);
                }
            }
            return this.row;
        }
        catch (Exception e) {
            throw new SerDeException(e);
        }
        finally {
            if (csv != null) {
                try {
                    csv.close();
                }
                catch (Exception e2) {
                    OpenCSVSerde.LOG.error("fail to close csv writer ", e2);
                }
            }
        }
    }
    
    private CSVReader newReader(final Reader reader, final char separator, final char quote, final char escape) {
        if ('\"' == escape) {
            return new CSVReader(reader, separator, quote);
        }
        return new CSVReader(reader, separator, quote, escape);
    }
    
    private CSVWriter newWriter(final Writer writer, final char separator, final char quote, final char escape) {
        if ('\"' == escape) {
            return new CSVWriter(writer, separator, quote, "");
        }
        return new CSVWriter(writer, separator, quote, escape, "");
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.inspector;
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(OpenCSVSerde.class.getName());
    }
}
