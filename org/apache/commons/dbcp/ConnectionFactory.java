// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.SQLException;
import java.sql.Connection;

public interface ConnectionFactory
{
    Connection createConnection() throws SQLException;
}
