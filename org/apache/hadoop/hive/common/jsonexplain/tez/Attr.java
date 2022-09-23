// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain.tez;

public class Attr implements Comparable<Attr>
{
    String name;
    String value;
    
    public Attr(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public int compareTo(final Attr o) {
        return this.name.compareToIgnoreCase(o.name);
    }
    
    @Override
    public String toString() {
        return this.name + this.value;
    }
}
