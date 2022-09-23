// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import org.apache.avro.io.BinaryEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.io.Decoder;
import java.util.NoSuchElementException;
import org.apache.avro.AvroRuntimeException;
import java.io.EOFException;
import java.io.UnsupportedEncodingException;
import org.apache.avro.Schema;
import java.util.List;
import java.util.Collections;
import org.apache.avro.util.Utf8;
import java.util.Arrays;
import org.apache.avro.io.DecoderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import java.io.Closeable;
import java.util.Iterator;

public class DataFileStream<D> implements Iterator<D>, Iterable<D>, Closeable
{
    private DatumReader<D> reader;
    private long blockSize;
    private boolean availableBlock;
    private Header header;
    BinaryDecoder vin;
    BinaryDecoder datumIn;
    ByteBuffer blockBuffer;
    long blockCount;
    long blockRemaining;
    byte[] syncBuffer;
    private Codec codec;
    private DataBlock block;
    
    public DataFileStream(final InputStream in, final DatumReader<D> reader) throws IOException {
        this.availableBlock = false;
        this.datumIn = null;
        this.syncBuffer = new byte[16];
        this.block = null;
        this.reader = reader;
        this.initialize(in);
    }
    
    protected DataFileStream(final DatumReader<D> reader) throws IOException {
        this.availableBlock = false;
        this.datumIn = null;
        this.syncBuffer = new byte[16];
        this.block = null;
        this.reader = reader;
    }
    
    void initialize(final InputStream in) throws IOException {
        this.header = new Header();
        this.vin = DecoderFactory.get().binaryDecoder(in, this.vin);
        final byte[] magic = new byte[DataFileConstants.MAGIC.length];
        try {
            this.vin.readFixed(magic);
        }
        catch (IOException e) {
            throw new IOException("Not a data file.");
        }
        if (!Arrays.equals(DataFileConstants.MAGIC, magic)) {
            throw new IOException("Not a data file.");
        }
        long l = this.vin.readMapStart();
        if (l > 0L) {
            do {
                for (long i = 0L; i < l; ++i) {
                    final String key = this.vin.readString(null).toString();
                    final ByteBuffer value = this.vin.readBytes(null);
                    final byte[] bb = new byte[value.remaining()];
                    value.get(bb);
                    this.header.meta.put(key, bb);
                    this.header.metaKeyList.add(key);
                }
            } while ((l = this.vin.mapNext()) != 0L);
        }
        this.vin.readFixed(this.header.sync);
        this.header.metaKeyList = (List<String>)Collections.unmodifiableList((List<?>)this.header.metaKeyList);
        this.header.schema = Schema.parse(this.getMetaString("avro.schema"), false);
        this.codec = this.resolveCodec();
        this.reader.setSchema(this.header.schema);
    }
    
    void initialize(final InputStream in, final Header header) throws IOException {
        this.header = header;
        this.codec = this.resolveCodec();
        this.reader.setSchema(header.schema);
    }
    
    Codec resolveCodec() {
        final String codecStr = this.getMetaString("avro.codec");
        if (codecStr != null) {
            return CodecFactory.fromString(codecStr).createInstance();
        }
        return CodecFactory.nullCodec().createInstance();
    }
    
    public Header getHeader() {
        return this.header;
    }
    
    public Schema getSchema() {
        return this.header.schema;
    }
    
    public List<String> getMetaKeys() {
        return this.header.metaKeyList;
    }
    
    public byte[] getMeta(final String key) {
        return this.header.meta.get(key);
    }
    
    public String getMetaString(final String key) {
        final byte[] value = this.getMeta(key);
        if (value == null) {
            return null;
        }
        try {
            return new String(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public long getMetaLong(final String key) {
        return Long.parseLong(this.getMetaString(key));
    }
    
    @Override
    public Iterator<D> iterator() {
        return this;
    }
    
    @Override
    public boolean hasNext() {
        try {
            if (this.blockRemaining == 0L) {
                if (null != this.datumIn) {
                    final boolean atEnd = this.datumIn.isEnd();
                    if (!atEnd) {
                        throw new IOException("Block read partially, the data may be corrupt");
                    }
                }
                if (this.hasNextBlock()) {
                    (this.block = this.nextRawBlock(this.block)).decompressUsing(this.codec);
                    this.blockBuffer = this.block.getAsByteBuffer();
                    this.datumIn = DecoderFactory.get().binaryDecoder(this.blockBuffer.array(), this.blockBuffer.arrayOffset() + this.blockBuffer.position(), this.blockBuffer.remaining(), this.datumIn);
                }
            }
            return this.blockRemaining != 0L;
        }
        catch (EOFException e2) {
            return false;
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    @Override
    public D next() {
        try {
            return this.next(null);
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    public D next(final D reuse) throws IOException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final D result = this.reader.read(reuse, this.datumIn);
        final long n = 0L;
        final long blockRemaining = this.blockRemaining - 1L;
        this.blockRemaining = blockRemaining;
        if (n == blockRemaining) {
            this.blockFinished();
        }
        return result;
    }
    
    public ByteBuffer nextBlock() throws IOException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.blockRemaining != this.blockCount) {
            throw new IllegalStateException("Not at block start.");
        }
        this.blockRemaining = 0L;
        this.datumIn = null;
        return this.blockBuffer;
    }
    
    public long getBlockCount() {
        return this.blockCount;
    }
    
    protected void blockFinished() throws IOException {
    }
    
    boolean hasNextBlock() {
        try {
            if (this.availableBlock) {
                return true;
            }
            if (this.vin.isEnd()) {
                return false;
            }
            this.blockRemaining = this.vin.readLong();
            this.blockSize = this.vin.readLong();
            if (this.blockSize > 2147483647L || this.blockSize < 0L) {
                throw new IOException("Block size invalid or too large for this implementation: " + this.blockSize);
            }
            this.blockCount = this.blockRemaining;
            return this.availableBlock = true;
        }
        catch (EOFException eof) {
            return false;
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    DataBlock nextRawBlock(DataBlock reuse) throws IOException {
        if (!this.hasNextBlock()) {
            throw new NoSuchElementException();
        }
        if (reuse == null || reuse.data.length < (int)this.blockSize) {
            reuse = new DataBlock(this.blockRemaining, (int)this.blockSize);
        }
        else {
            reuse.numEntries = this.blockRemaining;
            reuse.blockSize = (int)this.blockSize;
        }
        this.vin.readFixed(reuse.data, 0, reuse.blockSize);
        this.vin.readFixed(this.syncBuffer);
        if (!Arrays.equals(this.syncBuffer, this.header.sync)) {
            throw new IOException("Invalid sync!");
        }
        this.availableBlock = false;
        return reuse;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws IOException {
        this.vin.inputStream().close();
    }
    
    public static final class Header
    {
        Schema schema;
        Map<String, byte[]> meta;
        private transient List<String> metaKeyList;
        byte[] sync;
        
        private Header() {
            this.meta = new HashMap<String, byte[]>();
            this.metaKeyList = new ArrayList<String>();
            this.sync = new byte[16];
        }
    }
    
    static class DataBlock
    {
        private byte[] data;
        private long numEntries;
        private int blockSize;
        private int offset;
        private boolean flushOnWrite;
        
        private DataBlock(final long numEntries, final int blockSize) {
            this.offset = 0;
            this.flushOnWrite = true;
            this.data = new byte[blockSize];
            this.numEntries = numEntries;
            this.blockSize = blockSize;
        }
        
        DataBlock(final ByteBuffer block, final long numEntries) {
            this.offset = 0;
            this.flushOnWrite = true;
            this.data = block.array();
            this.blockSize = block.remaining();
            this.offset = block.arrayOffset() + block.position();
            this.numEntries = numEntries;
        }
        
        byte[] getData() {
            return this.data;
        }
        
        long getNumEntries() {
            return this.numEntries;
        }
        
        int getBlockSize() {
            return this.blockSize;
        }
        
        boolean isFlushOnWrite() {
            return this.flushOnWrite;
        }
        
        void setFlushOnWrite(final boolean flushOnWrite) {
            this.flushOnWrite = flushOnWrite;
        }
        
        ByteBuffer getAsByteBuffer() {
            return ByteBuffer.wrap(this.data, this.offset, this.blockSize);
        }
        
        void decompressUsing(final Codec c) throws IOException {
            final ByteBuffer result = c.decompress(this.getAsByteBuffer());
            this.data = result.array();
            this.blockSize = result.remaining();
        }
        
        void compressUsing(final Codec c) throws IOException {
            final ByteBuffer result = c.compress(this.getAsByteBuffer());
            this.data = result.array();
            this.blockSize = result.remaining();
        }
        
        void writeBlockTo(final BinaryEncoder e, final byte[] sync) throws IOException {
            e.writeLong(this.numEntries);
            e.writeLong(this.blockSize);
            e.writeFixed(this.data, this.offset, this.blockSize);
            e.writeFixed(sync);
            if (this.flushOnWrite) {
                e.flush();
            }
        }
    }
}
