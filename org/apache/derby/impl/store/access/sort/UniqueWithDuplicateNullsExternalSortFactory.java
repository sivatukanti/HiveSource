// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

public class UniqueWithDuplicateNullsExternalSortFactory extends ExternalSortFactory
{
    private static final String IMPLEMENTATIONID = "sort almost unique external";
    
    protected MergeSort getMergeSort() {
        return new UniqueWithDuplicateNullsMergeSort();
    }
    
    public String primaryImplementationType() {
        return "sort almost unique external";
    }
    
    public boolean supportsImplementation(final String anObject) {
        return "sort almost unique external".equals(anObject);
    }
}
