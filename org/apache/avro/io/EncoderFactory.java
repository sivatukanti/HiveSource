// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import org.apache.avro.AvroRuntimeException;
import org.codehaus.jackson.JsonGenerator;
import java.io.IOException;
import org.apache.avro.Schema;
import java.io.OutputStream;

public class EncoderFactory
{
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final int DEFAULT_BLOCK_BUFFER_SIZE = 65536;
    private static final int MIN_BLOCK_BUFFER_SIZE = 64;
    private static final int MAX_BLOCK_BUFFER_SIZE = 1073741824;
    private static final EncoderFactory DEFAULT_FACTORY;
    protected int binaryBufferSize;
    protected int binaryBlockSize;
    
    public EncoderFactory() {
        this.binaryBufferSize = 2048;
        this.binaryBlockSize = 65536;
    }
    
    public static EncoderFactory get() {
        return EncoderFactory.DEFAULT_FACTORY;
    }
    
    public EncoderFactory configureBufferSize(int size) {
        if (size < 32) {
            size = 32;
        }
        if (size > 16777216) {
            size = 16777216;
        }
        this.binaryBufferSize = size;
        return this;
    }
    
    public int getBufferSize() {
        return this.binaryBufferSize;
    }
    
    public EncoderFactory configureBlockSize(int size) {
        if (size < 64) {
            size = 64;
        }
        if (size > 1073741824) {
            size = 1073741824;
        }
        this.binaryBlockSize = size;
        return this;
    }
    
    public int getBlockSize() {
        return this.binaryBlockSize;
    }
    
    public BinaryEncoder binaryEncoder(final OutputStream out, final BinaryEncoder reuse) {
        if (null == reuse || !reuse.getClass().equals(BufferedBinaryEncoder.class)) {
            return new BufferedBinaryEncoder(out, this.binaryBufferSize);
        }
        return ((BufferedBinaryEncoder)reuse).configure(out, this.binaryBufferSize);
    }
    
    public BinaryEncoder directBinaryEncoder(final OutputStream out, final BinaryEncoder reuse) {
        if (null == reuse || !reuse.getClass().equals(DirectBinaryEncoder.class)) {
            return new DirectBinaryEncoder(out);
        }
        return ((DirectBinaryEncoder)reuse).configure(out);
    }
    
    public BinaryEncoder blockingBinaryEncoder(final OutputStream out, final BinaryEncoder reuse) {
        final int blockSize = this.binaryBlockSize;
        final int bufferSize = (blockSize * 2 >= this.binaryBufferSize) ? 32 : this.binaryBufferSize;
        if (null == reuse || !reuse.getClass().equals(BlockingBinaryEncoder.class)) {
            return new BlockingBinaryEncoder(out, blockSize, bufferSize);
        }
        return ((BlockingBinaryEncoder)reuse).configure(out, blockSize, bufferSize);
    }
    
    public JsonEncoder jsonEncoder(final Schema schema, final OutputStream out) throws IOException {
        return new JsonEncoder(schema, out);
    }
    
    public JsonEncoder jsonEncoder(final Schema schema, final OutputStream out, final boolean pretty) throws IOException {
        return new JsonEncoder(schema, out, pretty);
    }
    
    public JsonEncoder jsonEncoder(final Schema schema, final JsonGenerator gen) throws IOException {
        return new JsonEncoder(schema, gen);
    }
    
    public ValidatingEncoder validatingEncoder(final Schema schema, final Encoder encoder) throws IOException {
        return new ValidatingEncoder(schema, encoder);
    }
    
    static {
        DEFAULT_FACTORY = new DefaultEncoderFactory();
    }
    
    private static class DefaultEncoderFactory extends EncoderFactory
    {
        @Override
        public EncoderFactory configureBlockSize(final int size) {
            throw new AvroRuntimeException("Default EncoderFactory cannot be configured");
        }
        
        @Override
        public EncoderFactory configureBufferSize(final int size) {
            throw new AvroRuntimeException("Default EncoderFactory cannot be configured");
        }
    }
}
