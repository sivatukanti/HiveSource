// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.Timestamp;

public class SqlTimestampMapping extends TemporalMapping
{
    @Override
    public Class getJavaType() {
        return Timestamp.class;
    }
    
    @Override
    protected int getDefaultLengthAsString() {
        return 29;
    }
}
