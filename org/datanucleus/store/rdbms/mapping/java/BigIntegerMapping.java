// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.math.BigInteger;

public class BigIntegerMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return BigInteger.class;
    }
}
