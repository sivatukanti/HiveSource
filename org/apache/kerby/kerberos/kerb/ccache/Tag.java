// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.ccache;

public class Tag
{
    int tag;
    int tagLen;
    int time;
    int usec;
    int length;
    
    public Tag(final int tag, final int time, final int usec) {
        this.tag = 0;
        this.tagLen = 8;
        this.time = 0;
        this.usec = 0;
        this.length = 4 + this.tagLen;
        this.tag = tag;
        this.time = time;
        this.usec = usec;
    }
}
