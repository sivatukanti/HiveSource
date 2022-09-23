// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassNameConstants;
import java.util.BitSet;

public class BitSetMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return BitSet.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return ClassNameConstants.JAVA_IO_SERIALIZABLE;
    }
}
