// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv.bean;

import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;

public class ColumnPositionMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T>
{
    private String[] columnMapping;
    
    public ColumnPositionMappingStrategy() {
        this.columnMapping = new String[0];
    }
    
    @Override
    public void captureHeader(final CSVReader reader) throws IOException {
    }
    
    @Override
    protected String getColumnName(final int col) {
        return (null != this.columnMapping && col < this.columnMapping.length) ? this.columnMapping[col] : null;
    }
    
    public String[] getColumnMapping() {
        return (String[])((this.columnMapping != null) ? ((String[])this.columnMapping.clone()) : null);
    }
    
    public void setColumnMapping(final String[] columnMapping) {
        this.columnMapping = (String[])((columnMapping != null) ? ((String[])columnMapping.clone()) : null);
    }
}
