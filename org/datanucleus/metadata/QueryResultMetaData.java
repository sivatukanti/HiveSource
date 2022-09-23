// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import java.util.HashMap;
import org.datanucleus.util.StringUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class QueryResultMetaData extends MetaData
{
    protected final String name;
    protected List<PersistentTypeMapping> persistentTypeMappings;
    protected List<String> scalarColumns;
    
    public QueryResultMetaData(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addPersistentTypeMapping(final String className, final Map fieldColumnMap, final String discrimColumn) {
        if (this.persistentTypeMappings == null) {
            this.persistentTypeMappings = new ArrayList<PersistentTypeMapping>();
        }
        final PersistentTypeMapping m = new PersistentTypeMapping();
        m.className = className;
        m.discriminatorColumn = (StringUtils.isWhitespace(discrimColumn) ? null : discrimColumn);
        m.fieldColumnMap = fieldColumnMap;
        this.persistentTypeMappings.add(m);
    }
    
    public void addMappingForPersistentTypeMapping(final String className, final String fieldName, final String columnName) {
        PersistentTypeMapping m = null;
        if (this.persistentTypeMappings == null) {
            this.persistentTypeMappings = new ArrayList<PersistentTypeMapping>();
        }
        else {
            for (final PersistentTypeMapping mapping : this.persistentTypeMappings) {
                if (mapping.className.equals(className)) {
                    m = mapping;
                    break;
                }
            }
        }
        if (m == null) {
            m = new PersistentTypeMapping();
            m.className = className;
        }
        if (m.fieldColumnMap == null) {
            m.fieldColumnMap = new HashMap();
        }
        m.fieldColumnMap.put(fieldName, columnName);
    }
    
    public void addScalarColumn(final String columnName) {
        if (this.scalarColumns == null) {
            this.scalarColumns = new ArrayList<String>();
        }
        this.scalarColumns.add(columnName);
    }
    
    public PersistentTypeMapping[] getPersistentTypeMappings() {
        if (this.persistentTypeMappings == null) {
            return null;
        }
        return this.persistentTypeMappings.toArray(new PersistentTypeMapping[this.persistentTypeMappings.size()]);
    }
    
    public String[] getScalarColumns() {
        if (this.scalarColumns == null) {
            return null;
        }
        return this.scalarColumns.toArray(new String[this.scalarColumns.size()]);
    }
    
    public static class PersistentTypeMapping
    {
        String className;
        Map fieldColumnMap;
        String discriminatorColumn;
        
        public String getClassName() {
            return this.className;
        }
        
        public String getDiscriminatorColumn() {
            return this.discriminatorColumn;
        }
        
        public String getColumnForField(final String fieldName) {
            if (this.fieldColumnMap == null) {
                return null;
            }
            return this.fieldColumnMap.get(fieldName);
        }
    }
}
