// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class CeilFunction extends SimpleNumericMethod
{
    @Override
    protected String getFunctionName() {
        return "CEIL";
    }
    
    @Override
    protected Class getClassForMapping() {
        return Integer.TYPE;
    }
}
