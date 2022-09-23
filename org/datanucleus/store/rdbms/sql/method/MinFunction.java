// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class MinFunction extends SimpleOrderableAggregateMethod
{
    @Override
    protected String getFunctionName() {
        return "MIN";
    }
}
