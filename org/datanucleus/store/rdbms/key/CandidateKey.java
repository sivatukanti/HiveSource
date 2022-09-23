// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.key;

import java.util.Collection;
import java.util.List;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.Table;

public class CandidateKey extends Key
{
    public CandidateKey(final Table table) {
        super(table);
    }
    
    public void setColumn(final int seq, final Column col) {
        this.assertSameDatastoreObject(col);
        Key.setMinSize(this.columns, seq + 1);
        if (this.columns.get(seq) != null) {}
        this.columns.set(seq, col);
    }
    
    public int size() {
        return this.columns.size();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof CandidateKey && super.equals(obj));
    }
    
    @Override
    public String toString() {
        final StringBuffer s = new StringBuffer("UNIQUE ").append(Key.getColumnList(this.columns));
        return s.toString();
    }
}
