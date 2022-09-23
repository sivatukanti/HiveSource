// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.util.HashMap;
import java.util.Iterator;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.util.Map;

public class StatementParameterMapping
{
    Map<String, StatementMappingIndex> mappings;
    
    public StatementParameterMapping() {
        this.mappings = null;
    }
    
    public StatementMappingIndex getMappingForParameter(final String name) {
        if (this.mappings == null) {
            return null;
        }
        return this.mappings.get(name);
    }
    
    public StatementMappingIndex getMappingForParameterPosition(final int pos) {
        if (this.mappings == null) {
            return null;
        }
        for (final Map.Entry<String, StatementMappingIndex> entry : this.mappings.entrySet()) {
            final StatementMappingIndex idx = entry.getValue();
            for (int i = 0; i < idx.getNumberOfParameterOccurrences(); ++i) {
                final int[] positions = idx.getParameterPositionsForOccurrence(i);
                for (int j = 0; j < positions.length; ++j) {
                    if (positions[j] == pos) {
                        return idx;
                    }
                }
            }
        }
        return null;
    }
    
    public void addMappingForParameter(final String name, final StatementMappingIndex mapping) {
        if (this.mappings == null) {
            this.mappings = new HashMap<String, StatementMappingIndex>();
        }
        this.mappings.put(name, mapping);
    }
    
    public String[] getParameterNames() {
        if (this.mappings == null) {
            return null;
        }
        return this.mappings.keySet().toArray(new String[this.mappings.size()]);
    }
    
    public boolean isEmpty() {
        return this.mappings == null || this.mappings.size() == 0;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("StatementParameters:");
        if (this.mappings != null) {
            final Iterator<Map.Entry<String, StatementMappingIndex>> mapIter = this.mappings.entrySet().iterator();
            while (mapIter.hasNext()) {
                final Map.Entry<String, StatementMappingIndex> entry = mapIter.next();
                str.append(" param=").append(entry.getKey());
                str.append(" mapping=").append(entry.getValue());
                if (mapIter.hasNext()) {
                    str.append(",");
                }
            }
        }
        return str.toString();
    }
}
