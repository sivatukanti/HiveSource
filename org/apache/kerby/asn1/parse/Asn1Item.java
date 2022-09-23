// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import java.nio.ByteBuffer;

public class Asn1Item extends Asn1ParseResult
{
    public Asn1Item(final Asn1Header header, final int bodyStart, final ByteBuffer buffer) {
        super(header, bodyStart, buffer);
    }
    
    @Override
    public String toString() {
        return this.simpleInfo();
    }
}
