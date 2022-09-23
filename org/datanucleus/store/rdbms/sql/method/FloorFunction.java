// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class FloorFunction extends SimpleNumericMethod
{
    @Override
    protected String getFunctionName() {
        return "FLOOR";
    }
    
    @Override
    protected Class getClassForMapping() {
        return Integer.TYPE;
    }
}
