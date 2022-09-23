// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

public class CharacterMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return Character.class;
    }
    
    @Override
    public int getDefaultLength(final int index) {
        return 1;
    }
}
