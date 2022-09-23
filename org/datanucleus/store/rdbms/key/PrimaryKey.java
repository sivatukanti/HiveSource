// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.key;

import java.util.Collection;
import org.datanucleus.store.rdbms.table.Table;

public class PrimaryKey extends CandidateKey
{
    public PrimaryKey(final Table table) {
        super(table);
        this.name = table.getStoreManager().getIdentifierFactory().newPrimaryKeyIdentifier(table).getIdentifierName();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrimaryKey)) {
            return false;
        }
        final PrimaryKey pk = (PrimaryKey)obj;
        return pk.columns.size() == this.columns.size() && super.equals(obj);
    }
    
    @Override
    public String toString() {
        final StringBuffer s = new StringBuffer("PRIMARY KEY ").append(Key.getColumnList(this.columns));
        return s.toString();
    }
}
