// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db.dialect;

public class MsSQLDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "SELECT @@identity id";
    
    public String getSelectInsertId() {
        return "SELECT @@identity id";
    }
}
