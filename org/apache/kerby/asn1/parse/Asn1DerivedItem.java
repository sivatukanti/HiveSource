// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import java.util.Iterator;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.Tag;

public class Asn1DerivedItem extends Asn1Item
{
    private final Asn1Container container;
    private final Tag newTag;
    private int newBodyLength;
    private ByteBuffer newBodyBuffer;
    
    public Asn1DerivedItem(final Tag newTag, final Asn1Container container) {
        super(container.getHeader(), container.getBodyStart(), container.getBuffer());
        this.newTag = newTag;
        this.container = container;
        this.newBodyLength = -1;
    }
    
    @Override
    public Tag tag() {
        return this.newTag;
    }
    
    private int computeBodyLength() {
        int totalLen = 0;
        for (final Asn1ParseResult parseItem : this.container.getChildren()) {
            totalLen += parseItem.getBodyLength();
        }
        return totalLen;
    }
    
    private ByteBuffer makeBodyBuffer() {
        final ByteBuffer tmpBuffer = ByteBuffer.allocate(this.getBodyLength());
        for (final Asn1ParseResult parseItem : this.container.getChildren()) {
            tmpBuffer.put(parseItem.getBodyBuffer());
        }
        tmpBuffer.flip();
        return tmpBuffer;
    }
    
    @Override
    public ByteBuffer getBodyBuffer() {
        if (this.newBodyBuffer == null) {
            this.newBodyBuffer = this.makeBodyBuffer();
        }
        return this.newBodyBuffer;
    }
    
    @Override
    public int getBodyLength() {
        if (this.newBodyLength == -1) {
            this.newBodyLength = this.computeBodyLength();
        }
        return this.newBodyLength;
    }
}
