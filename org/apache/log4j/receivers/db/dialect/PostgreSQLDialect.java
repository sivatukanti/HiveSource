// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db.dialect;

public class PostgreSQLDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "SELECT currval('logging_event_id_seq')";
    
    public String getSelectInsertId() {
        return "SELECT currval('logging_event_id_seq')";
    }
}
