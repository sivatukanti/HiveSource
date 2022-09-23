// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.util.Iterator;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.util.HashMap;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.util.Map;

public class StatementResultMapping
{
    Map<Integer, Object> mappings;
    
    public StatementResultMapping() {
        this.mappings = null;
    }
    
    public Object getMappingForResultExpression(final int position) {
        if (this.mappings == null) {
            return null;
        }
        return this.mappings.get(position);
    }
    
    public void addMappingForResultExpression(final int position, final StatementMappingIndex mapping) {
        if (this.mappings == null) {
            this.mappings = new HashMap<Integer, Object>();
        }
        this.mappings.put(position, mapping);
    }
    
    public void addMappingForResultExpression(final int position, final StatementNewObjectMapping mapping) {
        if (this.mappings == null) {
            this.mappings = new HashMap<Integer, Object>();
        }
        this.mappings.put(position, mapping);
    }
    
    public void addMappingForResultExpression(final int position, final StatementClassMapping mapping) {
        if (this.mappings == null) {
            this.mappings = new HashMap<Integer, Object>();
        }
        this.mappings.put(position, mapping);
    }
    
    public boolean isEmpty() {
        return this.getNumberOfResultExpressions() == 0;
    }
    
    public int getNumberOfResultExpressions() {
        return (this.mappings != null) ? this.mappings.size() : 0;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("StatementResults:");
        if (this.mappings != null) {
            final Iterator<Map.Entry<Integer, Object>> mapIter = this.mappings.entrySet().iterator();
            while (mapIter.hasNext()) {
                final Map.Entry<Integer, Object> entry = mapIter.next();
                str.append(" position=").append(entry.getKey());
                str.append(" mapping=").append(entry.getValue());
                if (mapIter.hasNext()) {
                    str.append(",");
                }
            }
        }
        return str.toString();
    }
}
