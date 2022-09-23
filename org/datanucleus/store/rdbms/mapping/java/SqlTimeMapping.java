// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.Time;

public class SqlTimeMapping extends TemporalMapping
{
    @Override
    public Class getJavaType() {
        return Time.class;
    }
    
    @Override
    protected int getDefaultLengthAsString() {
        return 8;
    }
}
