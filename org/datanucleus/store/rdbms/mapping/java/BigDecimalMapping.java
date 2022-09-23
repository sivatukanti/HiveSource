// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.math.BigDecimal;

public class BigDecimalMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return BigDecimal.class;
    }
}
