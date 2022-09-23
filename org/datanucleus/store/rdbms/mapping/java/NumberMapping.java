// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassNameConstants;

public class NumberMapping extends SingleFieldMapping
{
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return ClassNameConstants.JAVA_MATH_BIGDECIMAL;
    }
    
    @Override
    public Class getJavaType() {
        return Number.class;
    }
}
