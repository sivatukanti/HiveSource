// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.key;

import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.table.Column;
import java.util.Collection;
import org.datanucleus.store.rdbms.table.Table;

public class Index extends Key
{
    private final boolean isUnique;
    private final String extendedIndexSettings;
    
    public Index(final Table table, final boolean isUnique, final String extendedIndexSettings) {
        super(table);
        this.isUnique = isUnique;
        this.extendedIndexSettings = extendedIndexSettings;
    }
    
    public Index(final CandidateKey ck) {
        super(ck.getTable());
        this.isUnique = true;
        this.extendedIndexSettings = null;
        this.columns.addAll(ck.getColumns());
    }
    
    public Index(final ForeignKey fk) {
        super(fk.getTable());
        this.isUnique = false;
        this.extendedIndexSettings = null;
        this.columns.addAll(fk.getColumns());
    }
    
    public boolean getUnique() {
        return this.isUnique;
    }
    
    public void setColumn(final int seq, final Column col) {
        this.assertSameDatastoreObject(col);
        Key.setMinSize(this.columns, seq + 1);
        if (this.columns.get(seq) != null) {
            throw new NucleusException("Index part #" + seq + " for " + this.table + " already set").setFatal();
        }
        this.columns.set(seq, col);
    }
    
    public int size() {
        return this.columns.size();
    }
    
    @Override
    public int hashCode() {
        return (this.isUnique ? 0 : 1) ^ super.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Index)) {
            return false;
        }
        final Index idx = (Index)obj;
        return idx.isUnique == this.isUnique && super.equals(obj);
    }
    
    public String getExtendedIndexSettings() {
        return this.extendedIndexSettings;
    }
    
    @Override
    public String toString() {
        return this.getColumnList();
    }
}
