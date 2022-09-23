// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import java.sql.SQLException;
import java.sql.Connection;
import org.apache.commons.dbcp.ConnectionFactory;

public interface XAConnectionFactory extends ConnectionFactory
{
    TransactionRegistry getTransactionRegistry();
    
    Connection createConnection() throws SQLException;
}
