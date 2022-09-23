// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import java.sql.SQLException;
import java.sql.Connection;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.component.spi.Component;

public interface ConnectionSource extends Component, OptionHandler
{
    public static final int UNKNOWN_DIALECT = 0;
    public static final int POSTGRES_DIALECT = 1;
    public static final int MYSQL_DIALECT = 2;
    public static final int ORACLE_DIALECT = 3;
    public static final int MSSQL_DIALECT = 4;
    public static final int HSQL_DIALECT = 5;
    
    Connection getConnection() throws SQLException;
    
    int getSQLDialectCode();
    
    boolean supportsGetGeneratedKeys();
    
    boolean supportsBatchUpdates();
}
