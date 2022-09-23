// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class MaxFunction extends SimpleOrderableAggregateMethod
{
    @Override
    protected String getFunctionName() {
        return "MAX";
    }
}
