// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.util.Locale;
import java.util.Properties;

class ControlInfo
{
    static final String ESCAPE = "Escape";
    static final String DEFAULT_ESCAPE = "\\";
    static final String QUOTE = "Quote";
    static final String DEFAULT_QUOTE = "'";
    static final String COMMIT_COUNT = "CommitCount";
    static final String DEFAULT_COMMIT_COUNT = "0";
    static final String START_ROW = "StartRow";
    static final String DEFAULT_START_ROW = "1";
    static final String STOP_ROW = "StopRow";
    static final String DEFAULT_STOP_ROW = "0";
    static final String FIELD_SEPARATOR = "FieldSeparator";
    static final String DEFAULT_FIELD_SEPARATOR = ",";
    static final String RECORD_SEPARATOR = "RecordSeparator";
    static final String DEFAULT_RECORD_SEPARATOR;
    static final String COLUMN_DEFINITION = "ColumnDefinition";
    static final String DEFAULT_COLUMN_DEFINITION = "FALSE";
    static final String NULL_STRING = "Null";
    static final String DEFAULT_NULL_STRING = "NULL";
    static final String FORMAT = "Format";
    static final String DEFAULT_FORMAT = "ASCII_DELIMITED";
    static final String DB2_DELIMITED_FORMAT = "DB2_DELIMITED";
    static final String FIELD_START_DELIMITER = "FieldStartDelimiter";
    static final String DEFAULT_FIELD_START_DELIMITER = "\"";
    static final String FIELD_END_DELIMITER = "FieldEndDelimiter";
    static final String DEFAULT_FIELD_END_DELIMITER = "\"";
    static final String COLUMN_WIDTHS = "ColumnWidths";
    static final String MESSAGE_FILE = "MessageFile";
    static final String DEFAULT_VERSION = "1";
    static final String VERSION = "Version";
    static final String NEWLINE = "\n";
    static final String COMMA = ",";
    static final String SPACE = " ";
    static final String TAB = "\t";
    static final String CR = "\r";
    static final String LF = "\n";
    static final String CRLF = "\r\n";
    static final String LFCR = "\n\r";
    static final String FF = "\f";
    static final String EMPTY_LINE = "\n\n";
    static final String SEMICOLON = ";";
    static final String DATA_CODESET = "DataCodeset";
    static final String HAS_DELIMETER_AT_END = "HasDelimeterAtEnd";
    static final String INTERNAL_NONE = "None";
    static final String INTERNAL_TRUE = "True";
    static final String INTERNAL_FALSE = "False";
    static final String INTERNAL_TAB = "Tab";
    static final String INTERNAL_SPACE = "Space";
    static final String INTERNAL_CR = "CR";
    static final String INTERNAL_LF = "LF";
    static final String INTERNAL_CRLF = "CR-LF";
    static final String INTERNAL_LFCR = "LF-CR";
    static final String INTERNAL_COMMA = "Comma";
    static final String INTERNAL_SEMICOLON = "Semicolon";
    static final String INTERNAL_NEWLINE = "New Line";
    static final String INTERNAL_FF = "FF";
    static final String INTERNAL_EMPTY_LINE = "Empty line";
    private Properties currentProperties;
    
    public ControlInfo() throws Exception {
        this.getCurrentProperties();
        if (this.getFieldSeparator().indexOf(this.getRecordSeparator()) != -1) {
            throw LoadError.fieldAndRecordSeparatorsSubset();
        }
    }
    
    String getPropertyValue(final String key) throws Exception {
        return this.getCurrentProperties().getProperty(key);
    }
    
    private void loadDefaultValues() {
        (this.currentProperties = new Properties()).put("FieldSeparator", ",");
        this.currentProperties.put("RecordSeparator", ControlInfo.DEFAULT_RECORD_SEPARATOR);
        this.currentProperties.put("ColumnDefinition", "FALSE");
        this.currentProperties.put("Null", "NULL");
        this.currentProperties.put("Format", "ASCII_DELIMITED");
        this.currentProperties.put("FieldStartDelimiter", "\"");
        this.currentProperties.put("FieldEndDelimiter", "\"");
        this.currentProperties.put("Version", "1");
        this.currentProperties.put("HasDelimeterAtEnd", "False");
    }
    
    String getCurrentVersion() throws Exception {
        return "1";
    }
    
    String getFormat() throws Exception {
        return this.getCurrentProperties().getProperty("Format");
    }
    
    int[] getColumnWidths() {
        return null;
    }
    
    String getFieldSeparator() throws Exception {
        return this.mapFromUserFriendlyFieldDelimiters(this.getCurrentProperties().getProperty("FieldSeparator"));
    }
    
    String getFieldStartDelimiter() throws Exception {
        return this.getCurrentProperties().getProperty("FieldStartDelimiter");
    }
    
    String getFieldEndDelimiter() throws Exception {
        return this.getCurrentProperties().getProperty("FieldEndDelimiter");
    }
    
    String getRecordSeparator() throws Exception {
        return this.mapFromUserFriendlyRecordDelimiters(this.getCurrentProperties().getProperty("RecordSeparator"));
    }
    
    boolean getHasDelimiterAtEnd() throws Exception {
        return this.getCurrentProperties().getProperty("HasDelimeterAtEnd").equals("True");
    }
    
    String getHasDelimeterAtEndString() throws Exception {
        return this.getCurrentProperties().getProperty("HasDelimeterAtEnd");
    }
    
    String getNullString() throws Exception {
        return this.getCurrentProperties().getProperty("Null");
    }
    
    String getColumnDefinition() throws Exception {
        return this.getCurrentProperties().getProperty("ColumnDefinition");
    }
    
    private String mapFromUserFriendlyFieldDelimiters(String s) {
        if (s.toUpperCase(Locale.ENGLISH).equals("Tab".toUpperCase(Locale.ENGLISH))) {
            return "\t";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("Space".toUpperCase(Locale.ENGLISH))) {
            return " ";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("CR".toUpperCase(Locale.ENGLISH))) {
            return "\r";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("LF".toUpperCase(Locale.ENGLISH))) {
            return "\n";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("CR-LF".toUpperCase(Locale.ENGLISH))) {
            return "\r\n";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("LF-CR".toUpperCase(Locale.ENGLISH))) {
            return "\n\r";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("Comma".toUpperCase(Locale.ENGLISH))) {
            return ",";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("Semicolon".toUpperCase(Locale.ENGLISH))) {
            return ";";
        }
        s = this.commonToFieldAndRecordDelimiters(s, "\\n", '\n');
        s = this.commonToFieldAndRecordDelimiters(s, "\\t", '\t');
        s = this.commonToFieldAndRecordDelimiters(s, "\\r", '\r');
        s = this.commonToFieldAndRecordDelimiters(s, "\\f", '\f');
        return s;
    }
    
    private String commonToFieldAndRecordDelimiters(String string, final String s, final char c) {
        while (string.indexOf(s) != -1) {
            final int index = string.indexOf(s);
            string = string.substring(0, index) + c + string.substring(index + 2);
        }
        return string;
    }
    
    private String mapFromUserFriendlyRecordDelimiters(String s) {
        if (s.equals("\n")) {
            s = "New Line";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("New Line".toUpperCase(Locale.ENGLISH))) {
            return "\n";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("CR".toUpperCase(Locale.ENGLISH))) {
            return "\r";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("LF".toUpperCase(Locale.ENGLISH))) {
            return "\n";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("CR-LF".toUpperCase(Locale.ENGLISH))) {
            return "\r\n";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("LF-CR".toUpperCase(Locale.ENGLISH))) {
            return "\n\r";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("FF".toUpperCase(Locale.ENGLISH))) {
            return "\f";
        }
        if (s.toUpperCase(Locale.ENGLISH).equals("Empty line".toUpperCase(Locale.ENGLISH))) {
            return "\n\n";
        }
        s = this.commonToFieldAndRecordDelimiters(s, "\\n", '\n');
        s = this.commonToFieldAndRecordDelimiters(s, "\\t", '\t');
        s = this.commonToFieldAndRecordDelimiters(s, "\\r", '\r');
        s = this.commonToFieldAndRecordDelimiters(s, "\\f", '\f');
        return s;
    }
    
    String getDataCodeset() throws Exception {
        return this.getCurrentProperties().getProperty("DataCodeset");
    }
    
    Properties getCurrentProperties() throws Exception {
        if (this.currentProperties == null) {
            this.loadDefaultValues();
        }
        return this.currentProperties;
    }
    
    public void setColumnWidths(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("ColumnWidths", value);
        }
    }
    
    public void setFieldSeparator(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("FieldSeparator", value);
        }
    }
    
    public void setFieldStartDelimiter(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("FieldStartDelimiter", value);
        }
    }
    
    public void setFieldEndDelimiter(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("FieldEndDelimiter", value);
        }
    }
    
    public void setRecordSeparator(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("RecordSeparator", value);
        }
    }
    
    public void setHasDelimiterAtEnd(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("HasDelimeterAtEnd", value);
        }
    }
    
    public void setNullString(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("Null", value);
        }
    }
    
    public void setcolumnDefinition(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("ColumnDefinition", value);
        }
    }
    
    public void setDataCodeset(final String value) throws Exception {
        if (value != null) {
            this.currentProperties.setProperty("DataCodeset", value);
        }
    }
    
    public void setCharacterDelimiter(final String s) throws Exception {
        if (s != null) {
            this.setFieldStartDelimiter(s);
            this.setFieldEndDelimiter(s);
        }
    }
    
    public void setControlProperties(final String characterDelimiter, final String fieldSeparator, final String dataCodeset) throws Exception {
        this.setCharacterDelimiter(characterDelimiter);
        this.setFieldSeparator(fieldSeparator);
        this.setDataCodeset(dataCodeset);
        this.validateDelimiters();
    }
    
    private void validateDelimiters() throws Exception {
        final char char1 = this.getFieldSeparator().charAt(0);
        final char char2 = this.getFieldStartDelimiter().charAt(0);
        if (char2 == '.') {
            throw LoadError.periodAsCharDelimiterNotAllowed();
        }
        if (char1 == char2 || char1 == '.' || Character.isSpaceChar(char1) || Character.isSpaceChar(char2) || Character.digit(char1, 16) != -1 || Character.digit(char2, 16) != -1) {
            throw LoadError.delimitersAreNotMutuallyExclusive();
        }
    }
    
    static {
        DEFAULT_RECORD_SEPARATOR = System.getProperty("line.separator");
    }
}
