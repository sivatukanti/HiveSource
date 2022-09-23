// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.key;

import java.util.Iterator;
import org.datanucleus.exceptions.NucleusException;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.table.Column;
import java.util.List;
import org.datanucleus.store.rdbms.table.Table;

abstract class Key
{
    protected String name;
    protected Table table;
    protected List<Column> columns;
    
    protected Key(final Table table) {
        this.columns = new ArrayList<Column>();
        this.table = table;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public List<Column> getColumns() {
        return Collections.unmodifiableList((List<? extends Column>)this.columns);
    }
    
    public String getColumnList() {
        return getColumnList(this.columns);
    }
    
    public void addColumn(final Column col) {
        this.assertSameDatastoreObject(col);
        this.columns.add(col);
    }
    
    public boolean startsWith(final Key key) {
        final int kSize = key.columns.size();
        return kSize <= this.columns.size() && key.columns.equals(this.columns.subList(0, kSize));
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    protected void assertSameDatastoreObject(final Column col) {
        if (!this.table.equals(col.getTable())) {
            throw new NucleusException("Cannot add " + col + " as key column for " + this.table).setFatal();
        }
    }
    
    @Override
    public int hashCode() {
        return this.columns.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Key)) {
            return false;
        }
        final Key key = (Key)obj;
        return this.columns.containsAll(key.columns) && this.columns.size() == key.columns.size();
    }
    
    protected static void setMinSize(final List list, final int size) {
        while (list.size() < size) {
            list.add(null);
        }
    }
    
    public static String getColumnList(final Collection cols) {
        final StringBuffer s = new StringBuffer("(");
        final Iterator i = cols.iterator();
        while (i.hasNext()) {
            final Column col = i.next();
            if (col == null) {
                s.append('?');
            }
            else {
                s.append(col.getIdentifier());
            }
            if (i.hasNext()) {
                s.append(',');
            }
        }
        s.append(')');
        return s.toString();
    }
}
