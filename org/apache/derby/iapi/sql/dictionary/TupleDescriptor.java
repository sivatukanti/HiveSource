// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;

public class TupleDescriptor
{
    private DataDictionary dataDictionary;
    
    public TupleDescriptor() {
    }
    
    public TupleDescriptor(final DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }
    
    protected DataDictionary getDataDictionary() {
        return this.dataDictionary;
    }
    
    protected void setDataDictionary(final DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }
    
    public boolean isPersistent() {
        return true;
    }
    
    DependableFinder getDependableFinder(final int n) {
        return this.dataDictionary.getDependableFinder(n);
    }
    
    DependableFinder getColumnDependableFinder(final int n, final byte[] array) {
        return this.dataDictionary.getColumnDependableFinder(n, array);
    }
    
    public String getDescriptorType() {
        return null;
    }
    
    public String getDescriptorName() {
        return null;
    }
}
