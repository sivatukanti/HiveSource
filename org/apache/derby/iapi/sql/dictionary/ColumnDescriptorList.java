// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.util.Iterator;
import org.apache.derby.catalog.UUID;
import java.util.ArrayList;

public class ColumnDescriptorList extends ArrayList
{
    public void add(final UUID uuid, final ColumnDescriptor e) {
        this.add(e);
    }
    
    public ColumnDescriptor getColumnDescriptor(final UUID uuid, final String s) {
        ColumnDescriptor columnDescriptor = null;
        for (final ColumnDescriptor columnDescriptor2 : this) {
            if (s.equals(columnDescriptor2.getColumnName()) && uuid.equals(columnDescriptor2.getReferencingUUID())) {
                columnDescriptor = columnDescriptor2;
                break;
            }
        }
        return columnDescriptor;
    }
    
    public ColumnDescriptor getColumnDescriptor(final UUID uuid, final int n) {
        ColumnDescriptor columnDescriptor = null;
        for (final ColumnDescriptor columnDescriptor2 : this) {
            if (n == columnDescriptor2.getPosition() && uuid.equals(columnDescriptor2.getReferencingUUID())) {
                columnDescriptor = columnDescriptor2;
                break;
            }
        }
        return columnDescriptor;
    }
    
    public ColumnDescriptor elementAt(final int index) {
        return this.get(index);
    }
    
    public String[] getColumnNames() {
        final String[] array = new String[this.size()];
        for (int size = this.size(), i = 0; i < size; ++i) {
            array[i] = this.elementAt(i).getColumnName();
        }
        return array;
    }
}
