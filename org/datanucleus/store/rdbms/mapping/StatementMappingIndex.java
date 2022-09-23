// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import java.util.Iterator;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusException;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class StatementMappingIndex
{
    JavaTypeMapping mapping;
    int[] columnPositions;
    List<int[]> paramPositions;
    String columnName;
    
    public StatementMappingIndex(final JavaTypeMapping mapping) {
        this.paramPositions = null;
        this.mapping = mapping;
    }
    
    public JavaTypeMapping getMapping() {
        return this.mapping;
    }
    
    public void setMapping(final JavaTypeMapping mapping) {
        this.mapping = mapping;
    }
    
    public String getColumnAlias() {
        if (this.columnName != null) {
            return this.columnName;
        }
        if (this.mapping != null && this.mapping.getMemberMetaData() != null) {
            return this.mapping.getMemberMetaData().getName();
        }
        return null;
    }
    
    public void setColumnAlias(final String alias) {
        this.columnName = alias;
    }
    
    public int[] getColumnPositions() {
        return this.columnPositions;
    }
    
    public void setColumnPositions(final int[] pos) {
        this.columnPositions = pos;
    }
    
    public void addParameterOccurrence(final int[] positions) {
        if (this.paramPositions == null) {
            this.paramPositions = new ArrayList<int[]>();
        }
        if (this.mapping != null && positions.length != this.mapping.getNumberOfDatastoreMappings()) {
            throw new NucleusException("Mapping " + this.mapping + " cannot be " + positions.length + " parameters since it has " + this.mapping.getNumberOfDatastoreMappings() + " columns");
        }
        this.paramPositions.add(positions);
    }
    
    public void removeParameterOccurrence(final int[] positions) {
        if (this.paramPositions == null) {
            return;
        }
        this.paramPositions.remove(positions);
    }
    
    public int getNumberOfParameterOccurrences() {
        return (this.paramPositions != null) ? this.paramPositions.size() : 0;
    }
    
    public int[] getParameterPositionsForOccurrence(final int num) {
        if (this.paramPositions == null) {
            return null;
        }
        return this.paramPositions.get(num);
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        str.append("mapping: " + this.mapping);
        if (this.paramPositions != null) {
            str.append(" parameter(s): ");
            final Iterator<int[]> iter = this.paramPositions.iterator();
            while (iter.hasNext()) {
                final int[] positions = iter.next();
                str.append(StringUtils.intArrayToString(positions));
                if (iter.hasNext()) {
                    str.append(',');
                }
            }
        }
        if (this.columnPositions != null) {
            str.append(" column(s): " + StringUtils.intArrayToString(this.columnPositions));
        }
        return str.toString();
    }
}
