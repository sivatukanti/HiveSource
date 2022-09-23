// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.Schema;
import java.io.InputStream;

public class DecoderFactory
{
    private static final DecoderFactory DEFAULT_FACTORY;
    static final int DEFAULT_BUFFER_SIZE = 8192;
    int binaryDecoderBufferSize;
    
    public DecoderFactory() {
        this.binaryDecoderBufferSize = 8192;
    }
    
    @Deprecated
    public static DecoderFactory defaultFactory() {
        return get();
    }
    
    public static DecoderFactory get() {
        return DecoderFactory.DEFAULT_FACTORY;
    }
    
    public DecoderFactory configureDecoderBufferSize(int size) {
        if (size < 32) {
            size = 32;
        }
        if (size > 16777216) {
            size = 16777216;
        }
        this.binaryDecoderBufferSize = size;
        return this;
    }
    
    public int getConfiguredBufferSize() {
        return this.binaryDecoderBufferSize;
    }
    
    @Deprecated
    public BinaryDecoder createBinaryDecoder(final InputStream in, final BinaryDecoder reuse) {
        return this.binaryDecoder(in, reuse);
    }
    
    public BinaryDecoder binaryDecoder(final InputStream in, final BinaryDecoder reuse) {
        if (null == reuse || !reuse.getClass().equals(BinaryDecoder.class)) {
            return new BinaryDecoder(in, this.binaryDecoderBufferSize);
        }
        return reuse.configure(in, this.binaryDecoderBufferSize);
    }
    
    public BinaryDecoder directBinaryDecoder(final InputStream in, final BinaryDecoder reuse) {
        if (null == reuse || !reuse.getClass().equals(DirectBinaryDecoder.class)) {
            return new DirectBinaryDecoder(in);
        }
        return ((DirectBinaryDecoder)reuse).configure(in);
    }
    
    @Deprecated
    public BinaryDecoder createBinaryDecoder(final byte[] bytes, final int offset, final int length, final BinaryDecoder reuse) {
        if (null == reuse || !reuse.getClass().equals(BinaryDecoder.class)) {
            return new BinaryDecoder(bytes, offset, length);
        }
        return reuse.configure(bytes, offset, length);
    }
    
    public BinaryDecoder binaryDecoder(final byte[] bytes, final int offset, final int length, final BinaryDecoder reuse) {
        if (null == reuse || !reuse.getClass().equals(BinaryDecoder.class)) {
            return new BinaryDecoder(bytes, offset, length);
        }
        return reuse.configure(bytes, offset, length);
    }
    
    @Deprecated
    public BinaryDecoder createBinaryDecoder(final byte[] bytes, final BinaryDecoder reuse) {
        return this.binaryDecoder(bytes, 0, bytes.length, reuse);
    }
    
    public BinaryDecoder binaryDecoder(final byte[] bytes, final BinaryDecoder reuse) {
        return this.binaryDecoder(bytes, 0, bytes.length, reuse);
    }
    
    public JsonDecoder jsonDecoder(final Schema schema, final InputStream input) throws IOException {
        return new JsonDecoder(schema, input);
    }
    
    public JsonDecoder jsonDecoder(final Schema schema, final String input) throws IOException {
        return new JsonDecoder(schema, input);
    }
    
    public ValidatingDecoder validatingDecoder(final Schema schema, final Decoder wrapped) throws IOException {
        return new ValidatingDecoder(schema, wrapped);
    }
    
    public ResolvingDecoder resolvingDecoder(final Schema writer, final Schema reader, final Decoder wrapped) throws IOException {
        return new ResolvingDecoder(writer, reader, wrapped);
    }
    
    static {
        DEFAULT_FACTORY = new DefaultDecoderFactory();
    }
    
    private static class DefaultDecoderFactory extends DecoderFactory
    {
        @Override
        public DecoderFactory configureDecoderBufferSize(final int bufferSize) {
            throw new IllegalArgumentException("This Factory instance is Immutable");
        }
    }
}
