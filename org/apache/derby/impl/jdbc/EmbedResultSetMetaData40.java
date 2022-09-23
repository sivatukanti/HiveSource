// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;

public class EmbedResultSetMetaData40 extends EmbedResultSetMetaData
{
    public EmbedResultSetMetaData40(final ResultColumnDescriptor[] array) {
        super(array);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        return clazz.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw Util.generateCsSQLException("XJ128.S", clazz);
        }
    }
}
