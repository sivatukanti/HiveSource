// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.ParameterValueSet;
import java.sql.ParameterMetaData;

class EmbedParameterMetaData30 extends EmbedParameterSetMetaData implements ParameterMetaData
{
    EmbedParameterMetaData30(final ParameterValueSet set, final DataTypeDescriptor[] array) {
        super(set, array);
    }
    
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        return clazz.isInstance(this);
    }
    
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw Util.generateCsSQLException("XJ128.S", clazz);
        }
    }
}
