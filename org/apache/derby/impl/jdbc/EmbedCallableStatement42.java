// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLType;
import java.sql.SQLException;

public class EmbedCallableStatement42 extends EmbedCallableStatement40
{
    public EmbedCallableStatement42(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        super(embedConnection, s, n, n2, n3);
    }
    
    @Override
    public void registerOutParameter(final int n, final SQLType sqlType) throws SQLException {
        this.checkStatus();
        this.registerOutParameter(n, Util42.getTypeAsInt(sqlType));
    }
    
    @Override
    public void registerOutParameter(final int n, final SQLType sqlType, final int n2) throws SQLException {
        this.checkStatus();
        this.registerOutParameter(n, Util42.getTypeAsInt(sqlType), n2);
    }
    
    @Override
    public void registerOutParameter(final int n, final SQLType sqlType, final String s) throws SQLException {
        this.checkStatus();
        this.registerOutParameter(n, Util42.getTypeAsInt(sqlType), s);
    }
    
    @Override
    public void registerOutParameter(final String s, final SQLType sqlType) throws SQLException {
        this.checkStatus();
        this.registerOutParameter(s, Util42.getTypeAsInt(sqlType));
    }
    
    @Override
    public void registerOutParameter(final String s, final SQLType sqlType, final int n) throws SQLException {
        this.checkStatus();
        this.registerOutParameter(s, Util42.getTypeAsInt(sqlType), n);
    }
    
    @Override
    public void registerOutParameter(final String s, final SQLType sqlType, final String s2) throws SQLException {
        this.checkStatus();
        this.registerOutParameter(s, Util42.getTypeAsInt(sqlType), s2);
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
    
    @Override
    public void setObject(final String s, final Object o, final SQLType sqlType) throws SQLException {
        this.checkStatus();
        this.setObject(s, o, Util42.getTypeAsInt(sqlType));
    }
    
    @Override
    public void setObject(final String s, final Object o, final SQLType sqlType, final int n) throws SQLException {
        this.checkStatus();
        this.setObject(s, o, Util42.getTypeAsInt(sqlType), n);
    }
}
