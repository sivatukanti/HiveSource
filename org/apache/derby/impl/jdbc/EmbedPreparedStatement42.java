// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLType;
import java.sql.SQLException;

public class EmbedPreparedStatement42 extends EmbedPreparedStatement40
{
    public EmbedPreparedStatement42(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        super(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    @Override
    public void setObject(final int n, final Object o, final SQLType sqlType) throws SQLException {
        this.checkStatus();
        this.setObject(n, o, Util42.getTypeAsInt(sqlType));
    }
    
    @Override
    public void setObject(final int n, final Object o, final SQLType sqlType, final int n2) throws SQLException {
        this.checkStatus();
        this.setObject(n, o, Util42.getTypeAsInt(sqlType), n2);
    }
}
