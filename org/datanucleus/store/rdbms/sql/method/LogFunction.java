// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class LogFunction extends SimpleNumericMethod
{
    @Override
    protected String getFunctionName() {
        return "LOG";
    }
    
    @Override
    protected Class getClassForMapping() {
        return Double.TYPE;
    }
}
