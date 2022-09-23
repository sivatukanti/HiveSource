// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import java.io.IOException;
import org.apache.kerby.asn1.Tag;
import java.nio.ByteBuffer;

public final class Asn1Reader
{
    private ByteBuffer buffer;
    private int position;
    
    public ByteBuffer getBuffer() {
        return this.buffer;
    }
    
    public Asn1Header readHeader() throws IOException {
        final Tag tag = this.readTag();
        final int valueLength = this.readLength();
        final Asn1Header header = new Asn1Header(tag, valueLength);
        return header;
    }
    
    public Asn1Reader(final ByteBuffer buffer) {
        this.buffer = buffer;
        this.position = buffer.position();
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public boolean available() {
        return this.position < this.buffer.limit();
    }
    
    protected byte readByte() throws IOException {
        return this.buffer.get(this.position++);
    }
    
    private Tag readTag() throws IOException {
        final int tagFlags = this.readTagFlags();
        final int tagNo = this.readTagNo(tagFlags);
        return new Tag(tagFlags, tagNo);
    }
    
    private int readTagFlags() throws IOException {
        final int tagFlags = this.readByte() & 0xFF;
        return tagFlags;
    }
    
    private int readTagNo(final int tagFlags) throws IOException {
        int tagNo = tagFlags & 0x1F;
        if (tagNo == 31) {
            tagNo = 0;
            int b = this.readByte() & 0xFF;
            if ((b & 0x7F) == 0x0) {
                throw new IOException("Invalid high tag number found");
            }
            while (b >= 0 && (b & 0x80) != 0x0) {
                tagNo |= (b & 0x7F);
                tagNo <<= 7;
                b = this.readByte();
            }
            tagNo |= (b & 0x7F);
        }
        return tagNo;
    }
    
    private int readLength() throws IOException {
        int result = this.readByte() & 0xFF;
        if (result == 128) {
            return -1;
        }
        if (result > 127) {
            final int length = result & 0x7F;
            if (length > 4) {
                throw new IOException("Bad length of more than 4 bytes: " + length);
            }
            result = 0;
            for (int i = 0; i < length; ++i) {
                final int tmp = this.readByte() & 0xFF;
                result = (result << 8) + tmp;
            }
        }
        if (result < 0) {
            throw new IOException("Invalid length " + result);
        }
        return result;
    }
}
