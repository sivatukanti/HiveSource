// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;

public abstract class MultiMapping extends JavaTypeMapping
{
    protected JavaTypeMapping[] javaTypeMappings;
    protected int numberOfDatastoreMappings;
    
    public MultiMapping() {
        this.javaTypeMappings = new JavaTypeMapping[0];
        this.numberOfDatastoreMappings = 0;
    }
    
    public void addJavaTypeMapping(final JavaTypeMapping mapping) {
        final JavaTypeMapping[] jtm = this.javaTypeMappings;
        System.arraycopy(jtm, 0, this.javaTypeMappings = new JavaTypeMapping[jtm.length + 1], 0, jtm.length);
        this.javaTypeMappings[jtm.length] = mapping;
    }
    
    public JavaTypeMapping[] getJavaTypeMapping() {
        return this.javaTypeMappings;
    }
    
    @Override
    public int getNumberOfDatastoreMappings() {
        if (this.numberOfDatastoreMappings == 0) {
            int numDatastoreTmp = 0;
            for (int i = 0; i < this.javaTypeMappings.length; ++i) {
                numDatastoreTmp += this.javaTypeMappings[i].getNumberOfDatastoreMappings();
            }
            this.numberOfDatastoreMappings = numDatastoreTmp;
        }
        return this.numberOfDatastoreMappings;
    }
    
    @Override
    public DatastoreMapping[] getDatastoreMappings() {
        if (this.datastoreMappings.length == 0) {
            final DatastoreMapping[] colMappings = new DatastoreMapping[this.getNumberOfDatastoreMappings()];
            int num = 0;
            for (int i = 0; i < this.javaTypeMappings.length; ++i) {
                for (int j = 0; j < this.javaTypeMappings[i].getNumberOfDatastoreMappings(); ++j) {
                    colMappings[num++] = this.javaTypeMappings[i].getDatastoreMapping(j);
                }
            }
            this.datastoreMappings = colMappings;
        }
        return super.getDatastoreMappings();
    }
    
    @Override
    public DatastoreMapping getDatastoreMapping(final int index) {
        if (index >= this.getNumberOfDatastoreMappings()) {
            throw new NucleusException("Attempt to get DatastoreMapping with index " + index + " when total number of mappings is " + this.numberOfDatastoreMappings + " for field=" + this.mmd).setFatal();
        }
        int currentIndex = 0;
        for (int numberJavaMappings = this.javaTypeMappings.length, i = 0; i < numberJavaMappings; ++i) {
            for (int numberDatastoreMappings = this.javaTypeMappings[i].getNumberOfDatastoreMappings(), j = 0; j < numberDatastoreMappings; ++j) {
                if (currentIndex == index) {
                    return this.javaTypeMappings[i].getDatastoreMapping(j);
                }
                ++currentIndex;
            }
        }
        throw new NucleusException("Invalid index " + index + " for DatastoreMapping (numColumns=" + this.getNumberOfDatastoreMappings() + "), for field=" + this.mmd).setFatal();
    }
}
