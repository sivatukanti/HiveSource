// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassNameConstants;

public class SerialisedMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return Object.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return ClassNameConstants.JAVA_IO_SERIALIZABLE;
    }
}
