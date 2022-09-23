// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.Reader;
import java.io.InputStream;

abstract class ExportWriteDataAbstract
{
    protected ControlInfo controlFileReader;
    protected int[] columnLengths;
    protected String fieldSeparator;
    protected String recordSeparator;
    protected String nullString;
    protected String columnDefinition;
    protected String format;
    protected String fieldStartDelimiter;
    protected String fieldStopDelimiter;
    protected String dataCodeset;
    protected String dataLocale;
    protected boolean hasDelimiterAtEnd;
    protected boolean doubleDelimiter;
    
    ExportWriteDataAbstract() {
        this.doubleDelimiter = true;
    }
    
    protected void loadPropertiesInfo() throws Exception {
        this.fieldSeparator = this.controlFileReader.getFieldSeparator();
        this.recordSeparator = this.controlFileReader.getRecordSeparator();
        this.nullString = this.controlFileReader.getNullString();
        this.columnDefinition = this.controlFileReader.getColumnDefinition();
        this.format = this.controlFileReader.getFormat();
        this.fieldStartDelimiter = this.controlFileReader.getFieldStartDelimiter();
        this.fieldStopDelimiter = this.controlFileReader.getFieldEndDelimiter();
        this.dataCodeset = this.controlFileReader.getDataCodeset();
        this.hasDelimiterAtEnd = this.controlFileReader.getHasDelimiterAtEnd();
    }
    
    abstract void writeColumnDefinitionOptionally(final String[] p0, final String[] p1) throws Exception;
    
    public void setColumnLengths(final int[] columnLengths) {
        this.columnLengths = columnLengths;
    }
    
    public abstract void writeData(final String[] p0, final boolean[] p1) throws Exception;
    
    abstract String writeBinaryColumnToExternalFile(final InputStream p0) throws Exception;
    
    abstract String writeCharColumnToExternalFile(final Reader p0) throws Exception;
    
    public abstract void noMoreRows() throws Exception;
}
