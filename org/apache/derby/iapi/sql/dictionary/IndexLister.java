// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;

public class IndexLister
{
    private TableDescriptor tableDescriptor;
    private IndexRowGenerator[] indexRowGenerators;
    private long[] indexConglomerateNumbers;
    private String[] indexNames;
    private IndexRowGenerator[] distinctIndexRowGenerators;
    private long[] distinctIndexConglomerateNumbers;
    private String[] distinctIndexNames;
    
    public IndexLister(final TableDescriptor tableDescriptor) {
        this.tableDescriptor = tableDescriptor;
    }
    
    public IndexRowGenerator[] getIndexRowGenerators() throws StandardException {
        if (this.indexRowGenerators == null) {
            this.getAllIndexes();
        }
        return this.indexRowGenerators;
    }
    
    public long[] getIndexConglomerateNumbers() throws StandardException {
        if (this.indexConglomerateNumbers == null) {
            this.getAllIndexes();
        }
        return this.indexConglomerateNumbers;
    }
    
    public String[] getIndexNames() throws StandardException {
        if (this.indexNames == null) {
            this.getAllIndexes();
        }
        return this.indexNames;
    }
    
    public IndexRowGenerator[] getDistinctIndexRowGenerators() throws StandardException {
        if (this.distinctIndexRowGenerators == null) {
            this.getAllIndexes();
        }
        return this.distinctIndexRowGenerators;
    }
    
    public long[] getDistinctIndexConglomerateNumbers() throws StandardException {
        if (this.distinctIndexConglomerateNumbers == null) {
            this.getAllIndexes();
        }
        return this.distinctIndexConglomerateNumbers;
    }
    
    public String[] getDistinctIndexNames() throws StandardException {
        if (this.indexNames == null) {
            this.getAllIndexes();
        }
        return this.indexNames;
    }
    
    private void getAllIndexes() throws StandardException {
        int n = 0;
        final ConglomerateDescriptor[] conglomerateDescriptors = this.tableDescriptor.getConglomerateDescriptors();
        final long[] array = new long[conglomerateDescriptors.length - 1];
        int n2 = 0;
        int n3 = array.length - 1;
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[i];
            if (conglomerateDescriptor.isIndex()) {
                final long conglomerateNumber = conglomerateDescriptor.getConglomerateNumber();
                int j;
                for (j = 0; j < n2; ++j) {
                    if (array[j] == conglomerateNumber) {
                        array[n3--] = i;
                        break;
                    }
                }
                if (j == n2) {
                    array[n2++] = conglomerateNumber;
                }
                ++n;
            }
        }
        this.indexRowGenerators = new IndexRowGenerator[n];
        this.indexConglomerateNumbers = new long[n];
        this.indexNames = new String[n];
        this.distinctIndexRowGenerators = new IndexRowGenerator[n2];
        this.distinctIndexConglomerateNumbers = new long[n2];
        this.distinctIndexNames = new String[n2];
        int n4 = array.length - 1;
        int k = 0;
        int n5 = -1;
        int n6 = -1;
        while (k < conglomerateDescriptors.length) {
            final ConglomerateDescriptor conglomerateDescriptor2 = conglomerateDescriptors[k];
            if (conglomerateDescriptor2.isIndex()) {
                this.indexRowGenerators[++n5] = conglomerateDescriptor2.getIndexDescriptor();
                this.indexConglomerateNumbers[n5] = conglomerateDescriptor2.getConglomerateNumber();
                if (!conglomerateDescriptor2.isConstraint()) {
                    this.indexNames[n5] = conglomerateDescriptor2.getConglomerateName();
                }
                if (n4 > n3 && k == (int)array[n4]) {
                    --n4;
                }
                else {
                    this.distinctIndexRowGenerators[++n6] = this.indexRowGenerators[n5];
                    this.distinctIndexConglomerateNumbers[n6] = this.indexConglomerateNumbers[n5];
                    this.distinctIndexNames[n6] = this.indexNames[n5];
                }
            }
            ++k;
        }
    }
}
