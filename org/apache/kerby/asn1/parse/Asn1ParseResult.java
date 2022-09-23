// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import org.apache.kerby.asn1.util.Asn1Util;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.type.Asn1Object;

public abstract class Asn1ParseResult extends Asn1Object
{
    private Asn1Header header;
    private int bodyStart;
    private int bodyEnd;
    private ByteBuffer buffer;
    
    public Asn1ParseResult(final Asn1Header header, final int bodyStart, final ByteBuffer buffer) {
        super(header.getTag());
        this.header = header;
        this.bodyStart = bodyStart;
        this.buffer = buffer;
        this.bodyEnd = (this.isDefinitiveLength() ? (bodyStart + header.getLength()) : -1);
    }
    
    public Asn1Header getHeader() {
        return this.header;
    }
    
    public int getBodyStart() {
        return this.bodyStart;
    }
    
    public int getBodyEnd() {
        return this.bodyEnd;
    }
    
    public void setBodyEnd(final int bodyEnd) {
        this.bodyEnd = bodyEnd;
    }
    
    public ByteBuffer getBuffer() {
        return this.buffer;
    }
    
    public ByteBuffer getBodyBuffer() {
        final ByteBuffer result = this.buffer.duplicate();
        result.position(this.bodyStart);
        final int end = this.getBodyEnd();
        if (end >= this.bodyStart) {
            result.limit(end);
        }
        return result;
    }
    
    public byte[] readBodyBytes() {
        final ByteBuffer bodyBuffer = this.getBodyBuffer();
        final byte[] result = new byte[bodyBuffer.remaining()];
        bodyBuffer.get(result);
        return result;
    }
    
    public boolean isDefinitiveLength() {
        return this.header.isDefinitiveLength();
    }
    
    public int getEncodingLength() {
        return this.getHeaderLength() + this.getBodyLength();
    }
    
    public int getHeaderLength() {
        final int bodyLen = this.getBodyLength();
        int headerLen = Asn1Util.lengthOfTagLength(this.header.getTag().tagNo());
        headerLen += (this.header.isDefinitiveLength() ? Asn1Util.lengthOfBodyLength(bodyLen) : 1);
        return headerLen;
    }
    
    public int getOffset() {
        return this.getBodyStart() - this.getHeaderLength();
    }
    
    public int getBodyLength() {
        if (this.isDefinitiveLength()) {
            return this.header.getLength();
        }
        if (this.getBodyEnd() != -1) {
            return this.getBodyEnd() - this.getBodyStart();
        }
        return -1;
    }
    
    public boolean checkBodyFinished(final int pos) {
        return this.getBodyEnd() != -1 && pos >= this.getBodyEnd();
    }
    
    public String simpleInfo() {
        return this.tag().typeStr() + " [" + "tag=" + this.tag() + ", off=" + this.getOffset() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "]";
    }
}
