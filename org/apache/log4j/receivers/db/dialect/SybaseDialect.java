// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db.dialect;

public class SybaseDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "select @@identity";
    
    public String getSelectInsertId() {
        return "select @@identity";
    }
}
