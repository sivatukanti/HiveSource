// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.ClassNameConstants;

public abstract class ObjectAsIntegerMapping extends SingleFieldMapping
{
    @Override
    public abstract Class getJavaType();
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return ClassNameConstants.JAVA_LANG_INTEGER;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        this.getDatastoreMapping(0).setObject(ps, exprIndex[0], this.objectToNumber(value));
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        final Object datastoreValue = this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        Object value = null;
        if (datastoreValue != null) {
            value = this.numberToObject((Number)datastoreValue);
        }
        return value;
    }
    
    protected abstract Number objectToNumber(final Object p0);
    
    protected abstract Object numberToObject(final Number p0);
}
