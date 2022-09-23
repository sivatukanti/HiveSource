// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.StringBufferStringConverter;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.types.converters.TypeConverter;

public class StringBufferMapping extends StringMapping
{
    protected static final TypeConverter<StringBuffer, String> converter;
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return ClassNameConstants.JAVA_LANG_STRING;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        final Object v = StringBufferMapping.converter.toDatastoreType((StringBuffer)value);
        super.setObject(ec, ps, exprIndex, v);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        final Object value = this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        if (value != null) {
            return StringBufferMapping.converter.toMemberType((String)value);
        }
        return null;
    }
    
    @Override
    public Class getJavaType() {
        return StringBuffer.class;
    }
    
    static {
        converter = new StringBufferStringConverter();
    }
}
