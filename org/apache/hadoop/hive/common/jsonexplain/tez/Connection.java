// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain.tez;

public class Connection
{
    public String type;
    public Vertex from;
    
    public Connection(final String type, final Vertex from) {
        this.type = type;
        this.from = from;
    }
}
