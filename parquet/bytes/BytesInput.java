// 
// Decompiled by Procyon v0.5.36
// 

package parquet.bytes;

import java.util.Iterator;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Arrays;
import parquet.Log;

public abstract class BytesInput
{
    private static final Log LOG;
    private static final boolean DEBUG = false;
    private static final EmptyBytesInput EMPTY_BYTES_INPUT;
    
    public static BytesInput concat(final BytesInput... inputs) {
        return new SequenceBytesIn((List)Arrays.asList(inputs));
    }
    
    public static BytesInput concat(final List<BytesInput> inputs) {
        return new SequenceBytesIn((List)inputs);
    }
    
    public static BytesInput from(final InputStream in, final int bytes) {
        return new StreamBytesInput(in, bytes);
    }
    
    public static BytesInput from(final byte[] in) {
        return new ByteArrayBytesInput(in, 0, in.length);
    }
    
    public static BytesInput from(final byte[] in, final int offset, final int length) {
        return new ByteArrayBytesInput(in, offset, length);
    }
    
    public static BytesInput fromInt(final int intValue) {
        return new IntBytesInput(intValue);
    }
    
    public static BytesInput fromUnsignedVarInt(final int intValue) {
        return new UnsignedVarIntBytesInput(intValue);
    }
    
    public static BytesInput fromZigZagVarInt(final int intValue) {
        final int zigZag = intValue << 1 ^ intValue >> 31;
        return new UnsignedVarIntBytesInput(zigZag);
    }
    
    public static BytesInput from(final CapacityByteArrayOutputStream arrayOut) {
        return new CapacityBAOSBytesInput(arrayOut);
    }
    
    public static BytesInput from(final ByteArrayOutputStream baos) {
        return new BAOSBytesInput(baos);
    }
    
    public static BytesInput empty() {
        return BytesInput.EMPTY_BYTES_INPUT;
    }
    
    public static BytesInput copy(final BytesInput bytesInput) throws IOException {
        return from(bytesInput.toByteArray());
    }
    
    public abstract void writeAllTo(final OutputStream p0) throws IOException;
    
    public byte[] toByteArray() throws IOException {
        final BAOS baos = new BAOS((int)this.size());
        this.writeAllTo(baos);
        return baos.getBuf();
    }
    
    public abstract long size();
    
    static {
        LOG = Log.getLog(BytesInput.class);
        EMPTY_BYTES_INPUT = new EmptyBytesInput();
    }
    
    private static final class BAOS extends ByteArrayOutputStream
    {
        private BAOS(final int size) {
            super(size);
        }
        
        public byte[] getBuf() {
            return this.buf;
        }
    }
    
    private static class StreamBytesInput extends BytesInput
    {
        private static final Log LOG;
        private final InputStream in;
        private final int byteCount;
        
        private StreamBytesInput(final InputStream in, final int byteCount) {
            this.in = in;
            this.byteCount = byteCount;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            out.write(this.toByteArray());
        }
        
        @Override
        public byte[] toByteArray() throws IOException {
            final byte[] buf = new byte[this.byteCount];
            new DataInputStream(this.in).readFully(buf);
            return buf;
        }
        
        @Override
        public long size() {
            return this.byteCount;
        }
        
        static {
            LOG = Log.getLog(StreamBytesInput.class);
        }
    }
    
    private static class SequenceBytesIn extends BytesInput
    {
        private static final Log LOG;
        private final List<BytesInput> inputs;
        private final long size;
        
        private SequenceBytesIn(final List<BytesInput> inputs) {
            this.inputs = inputs;
            long total = 0L;
            for (final BytesInput input : inputs) {
                total += input.size();
            }
            this.size = total;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            for (final BytesInput input : this.inputs) {
                input.writeAllTo(out);
            }
        }
        
        @Override
        public long size() {
            return this.size;
        }
        
        static {
            LOG = Log.getLog(SequenceBytesIn.class);
        }
    }
    
    private static class IntBytesInput extends BytesInput
    {
        private final int intValue;
        
        public IntBytesInput(final int intValue) {
            this.intValue = intValue;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            BytesUtils.writeIntLittleEndian(out, this.intValue);
        }
        
        @Override
        public long size() {
            return 4L;
        }
    }
    
    private static class UnsignedVarIntBytesInput extends BytesInput
    {
        private final int intValue;
        
        public UnsignedVarIntBytesInput(final int intValue) {
            this.intValue = intValue;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            BytesUtils.writeUnsignedVarInt(this.intValue, out);
        }
        
        @Override
        public long size() {
            final int s = 5 - (Integer.numberOfLeadingZeros(this.intValue) + 3) / 7;
            return (s == 0) ? 1L : s;
        }
    }
    
    private static class EmptyBytesInput extends BytesInput
    {
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
        }
        
        @Override
        public long size() {
            return 0L;
        }
    }
    
    private static class CapacityBAOSBytesInput extends BytesInput
    {
        private final CapacityByteArrayOutputStream arrayOut;
        
        private CapacityBAOSBytesInput(final CapacityByteArrayOutputStream arrayOut) {
            this.arrayOut = arrayOut;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            this.arrayOut.writeTo(out);
        }
        
        @Override
        public long size() {
            return this.arrayOut.size();
        }
    }
    
    private static class BAOSBytesInput extends BytesInput
    {
        private final ByteArrayOutputStream arrayOut;
        
        private BAOSBytesInput(final ByteArrayOutputStream arrayOut) {
            this.arrayOut = arrayOut;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            this.arrayOut.writeTo(out);
        }
        
        @Override
        public long size() {
            return this.arrayOut.size();
        }
    }
    
    private static class ByteArrayBytesInput extends BytesInput
    {
        private final byte[] in;
        private final int offset;
        private final int length;
        
        private ByteArrayBytesInput(final byte[] in, final int offset, final int length) {
            this.in = in;
            this.offset = offset;
            this.length = length;
        }
        
        @Override
        public void writeAllTo(final OutputStream out) throws IOException {
            out.write(this.in, this.offset, this.length);
        }
        
        @Override
        public long size() {
            return this.length;
        }
    }
}
