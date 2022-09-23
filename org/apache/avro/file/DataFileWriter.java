// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;
import org.apache.avro.io.Encoder;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.security.MessageDigest;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DatumReader;
import org.apache.avro.generic.GenericDatumReader;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;
import org.apache.avro.AvroRuntimeException;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.io.BinaryEncoder;
import java.io.OutputStream;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.Schema;
import java.io.Flushable;
import java.io.Closeable;

public class DataFileWriter<D> implements Closeable, Flushable
{
    private Schema schema;
    private DatumWriter<D> dout;
    private OutputStream underlyingStream;
    private BufferedFileOutputStream out;
    private BinaryEncoder vout;
    private final Map<String, byte[]> meta;
    private long blockCount;
    private NonCopyingByteArrayOutputStream buffer;
    private BinaryEncoder bufOut;
    private byte[] sync;
    private int syncInterval;
    private boolean isOpen;
    private Codec codec;
    private boolean flushOnEveryBlock;
    
    public DataFileWriter(final DatumWriter<D> dout) {
        this.meta = new HashMap<String, byte[]>();
        this.syncInterval = 64000;
        this.flushOnEveryBlock = true;
        this.dout = dout;
    }
    
    private void assertOpen() {
        if (!this.isOpen) {
            throw new AvroRuntimeException("not open");
        }
    }
    
    private void assertNotOpen() {
        if (this.isOpen) {
            throw new AvroRuntimeException("already open");
        }
    }
    
    public DataFileWriter<D> setCodec(final CodecFactory c) {
        this.assertNotOpen();
        this.codec = c.createInstance();
        this.setMetaInternal("avro.codec", this.codec.getName());
        return this;
    }
    
    public DataFileWriter<D> setSyncInterval(final int syncInterval) {
        if (syncInterval < 32 || syncInterval > 1073741824) {
            throw new IllegalArgumentException("Invalid syncInterval value: " + syncInterval);
        }
        this.syncInterval = syncInterval;
        return this;
    }
    
    public DataFileWriter<D> create(final Schema schema, final File file) throws IOException {
        return this.create(schema, new SyncableFileOutputStream(file));
    }
    
    public DataFileWriter<D> create(final Schema schema, final OutputStream outs) throws IOException {
        this.assertNotOpen();
        this.schema = schema;
        this.setMetaInternal("avro.schema", schema.toString());
        this.sync = generateSync();
        this.init(outs);
        this.vout.writeFixed(DataFileConstants.MAGIC);
        this.vout.writeMapStart();
        this.vout.setItemCount(this.meta.size());
        for (final Map.Entry<String, byte[]> entry : this.meta.entrySet()) {
            this.vout.startItem();
            this.vout.writeString(entry.getKey());
            this.vout.writeBytes(entry.getValue());
        }
        this.vout.writeMapEnd();
        this.vout.writeFixed(this.sync);
        this.vout.flush();
        return this;
    }
    
    public void setFlushOnEveryBlock(final boolean flushOnEveryBlock) {
        this.flushOnEveryBlock = flushOnEveryBlock;
    }
    
    public boolean isFlushOnEveryBlock() {
        return this.flushOnEveryBlock;
    }
    
    public DataFileWriter<D> appendTo(final File file) throws IOException {
        return this.appendTo(new SeekableFileInput(file), new SyncableFileOutputStream(file, true));
    }
    
    public DataFileWriter<D> appendTo(final SeekableInput in, final OutputStream out) throws IOException {
        this.assertNotOpen();
        final DataFileReader<D> reader = new DataFileReader<D>(in, new GenericDatumReader<D>());
        this.schema = reader.getSchema();
        this.sync = reader.getHeader().sync;
        this.meta.putAll(reader.getHeader().meta);
        final byte[] codecBytes = this.meta.get("avro.codec");
        if (codecBytes != null) {
            final String strCodec = new String(codecBytes, "UTF-8");
            this.codec = CodecFactory.fromString(strCodec).createInstance();
        }
        else {
            this.codec = CodecFactory.nullCodec().createInstance();
        }
        reader.close();
        this.init(out);
        return this;
    }
    
    private void init(final OutputStream outs) throws IOException {
        this.underlyingStream = outs;
        this.out = new BufferedFileOutputStream(outs);
        final EncoderFactory efactory = new EncoderFactory();
        this.vout = efactory.binaryEncoder(this.out, null);
        this.dout.setSchema(this.schema);
        this.buffer = new NonCopyingByteArrayOutputStream(Math.min((int)(this.syncInterval * 1.25), 1073741822));
        this.bufOut = efactory.binaryEncoder(this.buffer, null);
        if (this.codec == null) {
            this.codec = CodecFactory.nullCodec().createInstance();
        }
        this.isOpen = true;
    }
    
    private static byte[] generateSync() {
        try {
            final MessageDigest digester = MessageDigest.getInstance("MD5");
            final long time = System.currentTimeMillis();
            digester.update((UUID.randomUUID() + "@" + time).getBytes());
            return digester.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    private DataFileWriter<D> setMetaInternal(final String key, final byte[] value) {
        this.assertNotOpen();
        this.meta.put(key, value);
        return this;
    }
    
    private DataFileWriter<D> setMetaInternal(final String key, final String value) {
        try {
            return this.setMetaInternal(key, value.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public DataFileWriter<D> setMeta(final String key, final byte[] value) {
        if (isReservedMeta(key)) {
            throw new AvroRuntimeException("Cannot set reserved meta key: " + key);
        }
        return this.setMetaInternal(key, value);
    }
    
    public static boolean isReservedMeta(final String key) {
        return key.startsWith("avro.");
    }
    
    public DataFileWriter<D> setMeta(final String key, final String value) {
        try {
            return this.setMeta(key, value.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public DataFileWriter<D> setMeta(final String key, final long value) {
        return this.setMeta(key, Long.toString(value));
    }
    
    public void append(final D datum) throws IOException {
        this.assertOpen();
        final int usedBuffer = this.bufferInUse();
        try {
            this.dout.write(datum, this.bufOut);
        }
        catch (IOException e) {
            this.resetBufferTo(usedBuffer);
            throw new AppendWriteException(e);
        }
        catch (RuntimeException re) {
            this.resetBufferTo(usedBuffer);
            throw new AppendWriteException(re);
        }
        ++this.blockCount;
        this.writeIfBlockFull();
    }
    
    private void resetBufferTo(final int size) throws IOException {
        this.bufOut.flush();
        final byte[] data = this.buffer.toByteArray();
        this.buffer.reset();
        this.buffer.write(data, 0, size);
    }
    
    public void appendEncoded(final ByteBuffer datum) throws IOException {
        this.assertOpen();
        this.bufOut.writeFixed(datum);
        ++this.blockCount;
        this.writeIfBlockFull();
    }
    
    private int bufferInUse() {
        return this.buffer.size() + this.bufOut.bytesBuffered();
    }
    
    private void writeIfBlockFull() throws IOException {
        if (this.bufferInUse() >= this.syncInterval) {
            this.writeBlock();
        }
    }
    
    public void appendAllFrom(final DataFileStream<D> otherFile, final boolean recompress) throws IOException {
        this.assertOpen();
        final Schema otherSchema = otherFile.getSchema();
        if (!this.schema.equals(otherSchema)) {
            throw new IOException("Schema from file " + otherFile + " does not match");
        }
        this.writeBlock();
        final Codec otherCodec = otherFile.resolveCodec();
        DataFileStream.DataBlock nextBlockRaw = null;
        if (this.codec.equals(otherCodec) && !recompress) {
            while (otherFile.hasNextBlock()) {
                nextBlockRaw = otherFile.nextRawBlock(nextBlockRaw);
                nextBlockRaw.writeBlockTo(this.vout, this.sync);
            }
        }
        else {
            while (otherFile.hasNextBlock()) {
                nextBlockRaw = otherFile.nextRawBlock(nextBlockRaw);
                nextBlockRaw.decompressUsing(otherCodec);
                nextBlockRaw.compressUsing(this.codec);
                nextBlockRaw.writeBlockTo(this.vout, this.sync);
            }
        }
    }
    
    private void writeBlock() throws IOException {
        if (this.blockCount > 0L) {
            this.bufOut.flush();
            final ByteBuffer uncompressed = this.buffer.getByteArrayAsByteBuffer();
            final DataFileStream.DataBlock block = new DataFileStream.DataBlock(uncompressed, this.blockCount);
            block.setFlushOnWrite(this.flushOnEveryBlock);
            block.compressUsing(this.codec);
            block.writeBlockTo(this.vout, this.sync);
            this.buffer.reset();
            this.blockCount = 0L;
        }
    }
    
    public long sync() throws IOException {
        this.assertOpen();
        this.writeBlock();
        return this.out.tell();
    }
    
    @Override
    public void flush() throws IOException {
        this.sync();
        this.vout.flush();
    }
    
    public void fSync() throws IOException {
        this.flush();
        if (this.underlyingStream instanceof Syncable) {
            ((Syncable)this.underlyingStream).sync();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.isOpen) {
            this.flush();
            this.out.close();
            this.isOpen = false;
        }
    }
    
    public static class AppendWriteException extends RuntimeException
    {
        public AppendWriteException(final Exception e) {
            super(e);
        }
    }
    
    private class BufferedFileOutputStream extends BufferedOutputStream
    {
        private long position;
        
        public BufferedFileOutputStream(final OutputStream out) throws IOException {
            super(null);
            this.out = new PositionFilter(out);
        }
        
        public long tell() {
            return this.position + this.count;
        }
        
        private class PositionFilter extends FilterOutputStream
        {
            public PositionFilter(final OutputStream out) throws IOException {
                super(out);
            }
            
            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                this.out.write(b, off, len);
                BufferedFileOutputStream.this.position += len;
            }
        }
    }
    
    private static class NonCopyingByteArrayOutputStream extends ByteArrayOutputStream
    {
        NonCopyingByteArrayOutputStream(final int initialSize) {
            super(initialSize);
        }
        
        ByteBuffer getByteArrayAsByteBuffer() {
            return ByteBuffer.wrap(this.buf, 0, this.count);
        }
    }
}
