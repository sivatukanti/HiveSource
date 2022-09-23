// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.ConnectionFactory;

public interface XAConnectionFactory extends ConnectionFactory
{
    TransactionRegistry getTransactionRegistry();
    
    Connection createConnection() throws SQLException;
}
