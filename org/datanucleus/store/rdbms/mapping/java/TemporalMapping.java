// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassNameConstants;

public abstract class TemporalMapping extends SingleFieldMapping
{
    @Override
    public int getDefaultLength(final int index) {
        if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            return this.getDefaultLengthAsString();
        }
        return super.getDefaultLength(index);
    }
    
    protected abstract int getDefaultLengthAsString();
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            return ClassNameConstants.JAVA_LANG_STRING;
        }
        return super.getJavaTypeForDatastoreMapping(index);
    }
}
