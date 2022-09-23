// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import java.util.ArrayList;

public class ColumnSet
{
    public ArrayList<String> col;
    
    public ColumnSet() {
    }
    
    public ColumnSet(final ArrayList<String> col) {
        this();
        this.col = col;
    }
    
    @Override
    public String toString() {
        return this.col.toString();
    }
}
