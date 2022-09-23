// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.typed.Base64Variant;

public class SimpleValueEncoder
{
    protected final char[] mBuffer;
    protected final ValueEncoderFactory mEncoderFactory;
    
    public SimpleValueEncoder() {
        this.mBuffer = new char[500];
        this.mEncoderFactory = new ValueEncoderFactory();
    }
    
    public String encodeAsString(final int[] array, final int n, final int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(array, n, n2));
    }
    
    public String encodeAsString(final long[] array, final int n, final int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(array, n, n2));
    }
    
    public String encodeAsString(final float[] array, final int n, final int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(array, n, n2));
    }
    
    public String encodeAsString(final double[] array, final int n, final int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(array, n, n2));
    }
    
    public String encodeAsString(final Base64Variant base64Variant, final byte[] array, final int n, final int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(base64Variant, array, n, n2));
    }
    
    protected String encode(final AsciiValueEncoder asciiValueEncoder) {
        final int encodeMore = asciiValueEncoder.encodeMore(this.mBuffer, 0, this.mBuffer.length);
        if (asciiValueEncoder.isCompleted()) {
            return new String(this.mBuffer, 0, encodeMore);
        }
        final StringBuffer sb = new StringBuffer(this.mBuffer.length << 1);
        sb.append(this.mBuffer, 0, encodeMore);
        do {
            sb.append(this.mBuffer, 0, asciiValueEncoder.encodeMore(this.mBuffer, 0, this.mBuffer.length));
        } while (!asciiValueEncoder.isCompleted());
        return sb.toString();
    }
}
