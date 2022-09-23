// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLType;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.ResultSet;

public class EmbedResultSet42 extends EmbedResultSet40
{
    public EmbedResultSet42(final EmbedConnection embedConnection, final org.apache.derby.iapi.sql.ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        super(embedConnection, set, b, embedStatement, b2);
    }
    
    @Override
    public void updateObject(final int n, final Object o, final SQLType sqlType) throws SQLException {
        this.checkIfClosed("updateObject");
        this.updateObject(n, o, Util42.getTypeAsInt(sqlType));
    }
    
    @Override
    public void updateObject(final int n, final Object o, final SQLType sqlType, final int n2) throws SQLException {
        this.checkIfClosed("updateObject");
        this.updateObject(n, o, Util42.getTypeAsInt(sqlType));
        this.adjustScale(n, n2);
    }
    
    @Override
    public void updateObject(final String s, final Object o, final SQLType sqlType) throws SQLException {
        this.checkIfClosed("updateObject");
        this.updateObject(s, o, Util42.getTypeAsInt(sqlType));
    }
    
    @Override
    public void updateObject(final String s, final Object o, final SQLType sqlType, final int n) throws SQLException {
        this.checkIfClosed("updateObject");
        this.updateObject(this.findColumnName(s), o, sqlType, n);
    }
}
