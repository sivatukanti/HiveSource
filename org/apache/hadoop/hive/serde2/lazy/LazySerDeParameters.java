// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.Arrays;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.Text;
import java.util.Properties;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;

public class LazySerDeParameters implements LazyObjectInspectorParameters
{
    public static final byte[] DefaultSeparators;
    public static final String SERIALIZATION_EXTEND_NESTING_LEVELS = "hive.serialization.extend.nesting.levels";
    public static final String SERIALIZATION_EXTEND_ADDITIONAL_NESTING_LEVELS = "hive.serialization.extend.additional.nesting.levels";
    private Properties tableProperties;
    private String serdeName;
    private byte[] separators;
    private String nullString;
    private Text nullSequence;
    private TypeInfo rowTypeInfo;
    private boolean lastColumnTakesRest;
    private List<String> columnNames;
    private List<TypeInfo> columnTypes;
    private boolean escaped;
    private byte escapeChar;
    private boolean[] needsEscape;
    private boolean extendedBooleanLiteral;
    List<String> timestampFormats;
    
    public LazySerDeParameters(final Configuration job, final Properties tbl, final String serdeName) throws SerDeException {
        this.needsEscape = new boolean[256];
        this.tableProperties = tbl;
        this.serdeName = serdeName;
        this.nullString = tbl.getProperty("serialization.null.format", "\\N");
        this.nullSequence = new Text(this.nullString);
        final String lastColumnTakesRestString = tbl.getProperty("serialization.last.column.takes.rest");
        this.lastColumnTakesRest = (lastColumnTakesRestString != null && lastColumnTakesRestString.equalsIgnoreCase("true"));
        this.extractColumnInfo();
        this.rowTypeInfo = TypeInfoFactory.getStructTypeInfo(this.columnNames, this.columnTypes);
        this.collectSeparators(tbl);
        final String escapeProperty = tbl.getProperty("escape.delim");
        this.escaped = (escapeProperty != null);
        if (this.escaped) {
            this.escapeChar = LazyUtils.getByte(escapeProperty, (byte)92);
            this.needsEscape[this.escapeChar & 0xFF] = true;
            for (final byte b : this.separators) {
                this.needsEscape[b & 0xFF] = true;
            }
        }
        this.extendedBooleanLiteral = (job != null && job.getBoolean(HiveConf.ConfVars.HIVE_LAZYSIMPLE_EXTENDED_BOOLEAN_LITERAL.varname, false));
        final String[] timestampFormatsArray = HiveStringUtils.splitAndUnEscape(tbl.getProperty("timestamp.formats"));
        if (timestampFormatsArray != null) {
            this.timestampFormats = Arrays.asList(timestampFormatsArray);
        }
    }
    
    public void extractColumnInfo() throws SerDeException {
        final String columnNameProperty = this.tableProperties.getProperty("columns");
        String columnTypeProperty = this.tableProperties.getProperty("columns.types");
        if (columnNameProperty != null && columnNameProperty.length() > 0) {
            this.columnNames = Arrays.asList(columnNameProperty.split(","));
        }
        else {
            this.columnNames = new ArrayList<String>();
        }
        if (columnTypeProperty == null) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.columnNames.size(); ++i) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append("string");
            }
            columnTypeProperty = sb.toString();
        }
        this.columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(columnTypeProperty);
        if (this.columnNames.size() != this.columnTypes.size()) {
            throw new SerDeException(this.serdeName + ": columns has " + this.columnNames.size() + " elements while columns.types has " + this.columnTypes.size() + " elements!");
        }
    }
    
    public List<TypeInfo> getColumnTypes() {
        return this.columnTypes;
    }
    
    public List<String> getColumnNames() {
        return this.columnNames;
    }
    
    @Override
    public byte[] getSeparators() {
        return this.separators;
    }
    
    public String getNullString() {
        return this.nullString;
    }
    
    @Override
    public Text getNullSequence() {
        return this.nullSequence;
    }
    
    public TypeInfo getRowTypeInfo() {
        return this.rowTypeInfo;
    }
    
    @Override
    public boolean isLastColumnTakesRest() {
        return this.lastColumnTakesRest;
    }
    
    @Override
    public boolean isEscaped() {
        return this.escaped;
    }
    
    @Override
    public byte getEscapeChar() {
        return this.escapeChar;
    }
    
    public boolean[] getNeedsEscape() {
        return this.needsEscape;
    }
    
    @Override
    public boolean isExtendedBooleanLiteral() {
        return this.extendedBooleanLiteral;
    }
    
    @Override
    public List<String> getTimestampFormats() {
        return this.timestampFormats;
    }
    
    public void setSeparator(final int index, final byte separator) throws SerDeException {
        if (index < 0 || index >= this.separators.length) {
            throw new SerDeException("Invalid separator array index value: " + index);
        }
        this.separators[index] = separator;
    }
    
    private void collectSeparators(final Properties tableProperties) {
        final List<Byte> separatorCandidates = new ArrayList<Byte>();
        final String extendNestingValue = tableProperties.getProperty("hive.serialization.extend.nesting.levels");
        final String extendAdditionalNestingValue = tableProperties.getProperty("hive.serialization.extend.additional.nesting.levels");
        final boolean extendedNesting = extendNestingValue != null && extendNestingValue.equalsIgnoreCase("true");
        final boolean extendedAdditionalNesting = extendAdditionalNestingValue != null && extendAdditionalNestingValue.equalsIgnoreCase("true");
        separatorCandidates.add(LazyUtils.getByte(tableProperties.getProperty("field.delim", tableProperties.getProperty("serialization.format")), LazySerDeParameters.DefaultSeparators[0]));
        separatorCandidates.add(LazyUtils.getByte(tableProperties.getProperty("colelction.delim"), LazySerDeParameters.DefaultSeparators[1]));
        separatorCandidates.add(LazyUtils.getByte(tableProperties.getProperty("mapkey.delim"), LazySerDeParameters.DefaultSeparators[2]));
        for (byte b = 4; b <= 8; ++b) {
            separatorCandidates.add(b);
        }
        separatorCandidates.add((Byte)11);
        for (byte b = 14; b <= 26; ++b) {
            separatorCandidates.add(b);
        }
        for (byte b = 28; b <= 31; ++b) {
            separatorCandidates.add(b);
        }
        for (byte b = -128; b <= -1; ++b) {
            separatorCandidates.add(b);
        }
        int numSeparators = 8;
        if (extendedAdditionalNesting) {
            numSeparators = separatorCandidates.size();
        }
        else if (extendedNesting) {
            numSeparators = 24;
        }
        this.separators = new byte[numSeparators];
        for (int i = 0; i < numSeparators; ++i) {
            this.separators[i] = separatorCandidates.get(i);
        }
    }
    
    static {
        DefaultSeparators = new byte[] { 1, 2, 3 };
    }
}
