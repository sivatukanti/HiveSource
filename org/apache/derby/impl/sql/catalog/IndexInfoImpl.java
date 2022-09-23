// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;

class IndexInfoImpl
{
    private IndexRowGenerator irg;
    private long conglomerateNumber;
    private final CatalogRowFactory crf;
    private final int indexNumber;
    
    IndexInfoImpl(final int indexNumber, final CatalogRowFactory crf) {
        this.crf = crf;
        this.indexNumber = indexNumber;
        this.conglomerateNumber = -1L;
    }
    
    long getConglomerateNumber() {
        return this.conglomerateNumber;
    }
    
    void setConglomerateNumber(final long conglomerateNumber) {
        this.conglomerateNumber = conglomerateNumber;
    }
    
    String getIndexName() {
        return this.crf.getIndexName(this.indexNumber);
    }
    
    int getColumnCount() {
        return this.crf.getIndexColumnCount(this.indexNumber);
    }
    
    IndexRowGenerator getIndexRowGenerator() {
        return this.irg;
    }
    
    void setIndexRowGenerator(final IndexRowGenerator irg) {
        this.irg = irg;
    }
    
    int getBaseColumnPosition(final int n) {
        return this.crf.getIndexColumnPositions(this.indexNumber)[n];
    }
    
    boolean isIndexUnique() {
        return this.crf.isIndexUnique(this.indexNumber);
    }
}
