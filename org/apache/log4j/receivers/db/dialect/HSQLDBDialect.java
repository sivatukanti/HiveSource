// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db.dialect;

public class HSQLDBDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "CALL IDENTITY()";
    
    public String getSelectInsertId() {
        return "CALL IDENTITY()";
    }
}
