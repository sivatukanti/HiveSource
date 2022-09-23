// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import org.apache.kerby.asn1.Tag;

public class Asn1Header
{
    private Tag tag;
    private int length;
    
    public Asn1Header(final Tag tag, final int length) {
        this.tag = tag;
        this.length = length;
    }
    
    public Tag getTag() {
        return this.tag;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public boolean isEOC() {
        return this.length == 0 && this.tag.isEOC();
    }
    
    public boolean isDefinitiveLength() {
        return this.length != -1;
    }
}
