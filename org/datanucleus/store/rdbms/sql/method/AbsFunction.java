// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class AbsFunction extends SimpleNumericMethod
{
    @Override
    protected String getFunctionName() {
        return "ABS";
    }
    
    @Override
    protected Class getClassForMapping() {
        return Double.TYPE;
    }
}
