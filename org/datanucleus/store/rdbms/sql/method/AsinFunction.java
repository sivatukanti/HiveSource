// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class AsinFunction extends SimpleNumericMethod
{
    @Override
    protected String getFunctionName() {
        return "ASIN";
    }
    
    @Override
    protected Class getClassForMapping() {
        return Double.TYPE;
    }
}
