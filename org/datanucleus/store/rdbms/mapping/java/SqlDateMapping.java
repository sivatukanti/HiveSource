// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.Date;

public class SqlDateMapping extends TemporalMapping
{
    @Override
    public Class getJavaType() {
        return Date.class;
    }
    
    @Override
    protected int getDefaultLengthAsString() {
        return 10;
    }
}
