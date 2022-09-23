// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv.bean;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class HeaderColumnNameTranslateMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T>
{
    private Map<String, String> columnMapping;
    
    public HeaderColumnNameTranslateMappingStrategy() {
        this.columnMapping = new HashMap<String, String>();
    }
    
    @Override
    protected String getColumnName(final int col) {
        return (col < this.header.length) ? this.columnMapping.get(this.header[col].toUpperCase()) : null;
    }
    
    public Map<String, String> getColumnMapping() {
        return this.columnMapping;
    }
    
    public void setColumnMapping(final Map<String, String> columnMapping) {
        for (final String key : columnMapping.keySet()) {
            this.columnMapping.put(key.toUpperCase(), columnMapping.get(key));
        }
    }
}
