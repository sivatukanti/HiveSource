// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class AtanFunction extends SimpleNumericMethod
{
    @Override
    protected String getFunctionName() {
        return "ATAN";
    }
    
    @Override
    protected Class getClassForMapping() {
        return Double.TYPE;
    }
}
