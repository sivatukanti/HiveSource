// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db.dialect;

import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import org.apache.log4j.component.spi.ComponentBase;

public class Util extends ComponentBase
{
    private static final String POSTGRES_PART = "postgresql";
    private static final String MYSQL_PART = "mysql";
    private static final String ORACLE_PART = "oracle";
    private static final String MSSQL_PART = "microsoft";
    private static final String HSQL_PART = "hsql";
    
    public static int discoverSQLDialect(final DatabaseMetaData meta) {
        final int dialectCode = 0;
        try {
            final String dbName = meta.getDatabaseProductName().toLowerCase();
            if (dbName.indexOf("postgresql") != -1) {
                return 1;
            }
            if (dbName.indexOf("mysql") != -1) {
                return 2;
            }
            if (dbName.indexOf("oracle") != -1) {
                return 3;
            }
            if (dbName.indexOf("microsoft") != -1) {
                return 4;
            }
            if (dbName.indexOf("hsql") != -1) {
                return 5;
            }
            return 0;
        }
        catch (SQLException sqle) {
            return dialectCode;
        }
    }
    
    public static SQLDialect getDialectFromCode(final int dialectCode) {
        SQLDialect sqlDialect = null;
        switch (dialectCode) {
            case 1: {
                sqlDialect = new PostgreSQLDialect();
                break;
            }
            case 2: {
                sqlDialect = new MySQLDialect();
                break;
            }
            case 3: {
                sqlDialect = new OracleDialect();
                break;
            }
            case 4: {
                sqlDialect = new MsSQLDialect();
                break;
            }
            case 5: {
                sqlDialect = new HSQLDBDialect();
                break;
            }
        }
        return sqlDialect;
    }
    
    public boolean supportsGetGeneratedKeys(final DatabaseMetaData meta) {
        try {
            return (boolean)DatabaseMetaData.class.getMethod("supportsGetGeneratedKeys", (Class[])null).invoke(meta, (Object[])null);
        }
        catch (Throwable e) {
            this.getLogger().info("Could not call supportsGetGeneratedKeys method. This may be recoverable");
            return false;
        }
    }
    
    public boolean supportsBatchUpdates(final DatabaseMetaData meta) {
        try {
            return meta.supportsBatchUpdates();
        }
        catch (Throwable e) {
            this.getLogger().info("Missing DatabaseMetaData.supportsBatchUpdates method.");
            return false;
        }
    }
}
