// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.EOFException;
import org.apache.avro.io.DecoderFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.io.IOException;
import org.apache.avro.io.DatumReader;
import java.io.File;

public class DataFileReader<D> extends DataFileStream<D> implements FileReader<D>
{
    private SeekableInputStream sin;
    private long blockStart;
    
    public static <D> FileReader<D> openReader(final File file, final DatumReader<D> reader) throws IOException {
        return openReader(new SeekableFileInput(file), reader);
    }
    
    public static <D> FileReader<D> openReader(final SeekableInput in, final DatumReader<D> reader) throws IOException {
        if (in.length() < DataFileConstants.MAGIC.length) {
            throw new IOException("Not an Avro data file");
        }
        final byte[] magic = new byte[DataFileConstants.MAGIC.length];
        in.seek(0L);
        for (int c = 0; c < magic.length; c = in.read(magic, c, magic.length - c)) {}
        in.seek(0L);
        if (Arrays.equals(DataFileConstants.MAGIC, magic)) {
            return new DataFileReader<D>(in, reader);
        }
        if (Arrays.equals(DataFileReader12.MAGIC, magic)) {
            return new DataFileReader12<D>(in, reader);
        }
        throw new IOException("Not an Avro data file");
    }
    
    public static <D> DataFileReader<D> openReader(final SeekableInput in, final DatumReader<D> reader, final Header header, final boolean sync) throws IOException {
        final DataFileReader<D> dreader = new DataFileReader<D>(in, reader, header);
        if (sync) {
            dreader.sync(in.tell());
        }
        else {
            dreader.seek(in.tell());
        }
        return dreader;
    }
    
    public DataFileReader(final File file, final DatumReader<D> reader) throws IOException {
        this((SeekableInput)new SeekableFileInput(file), reader);
    }
    
    public DataFileReader(final SeekableInput sin, final DatumReader<D> reader) throws IOException {
        super(reader);
        this.initialize(this.sin = new SeekableInputStream(sin));
        this.blockFinished();
    }
    
    protected DataFileReader(final SeekableInput sin, final DatumReader<D> reader, final Header header) throws IOException {
        super(reader);
        this.initialize(this.sin = new SeekableInputStream(sin), header);
    }
    
    public void seek(final long position) throws IOException {
        this.sin.seek(position);
        this.vin = DecoderFactory.get().binaryDecoder(this.sin, this.vin);
        this.datumIn = null;
        this.blockRemaining = 0L;
        this.blockStart = position;
    }
    
    @Override
    public void sync(final long position) throws IOException {
        this.seek(position);
        if (position == 0L && this.getMeta("avro.sync") != null) {
            this.initialize(this.sin);
            return;
        }
        try {
            int i = 0;
            final InputStream in = this.vin.inputStream();
            this.vin.readFixed(this.syncBuffer);
            int b;
            do {
                int j;
                for (j = 0; j < 16 && this.getHeader().sync[j] == this.syncBuffer[(i + j) % 16]; ++j) {}
                if (j == 16) {
                    this.blockStart = position + i + 16L;
                    return;
                }
                b = in.read();
                this.syncBuffer[i++ % 16] = (byte)b;
            } while (b != -1);
        }
        catch (EOFException ex) {}
        this.blockStart = this.sin.tell();
    }
    
    @Override
    protected void blockFinished() throws IOException {
        this.blockStart = this.sin.tell() - this.vin.inputStream().available();
    }
    
    public long previousSync() {
        return this.blockStart;
    }
    
    @Override
    public boolean pastSync(final long position) throws IOException {
        return this.blockStart >= position + 16L || this.blockStart >= this.sin.length();
    }
    
    @Override
    public long tell() throws IOException {
        return this.sin.tell();
    }
    
    static class SeekableInputStream extends InputStream implements SeekableInput
    {
        private final byte[] oneByte;
        private SeekableInput in;
        
        SeekableInputStream(final SeekableInput in) throws IOException {
            this.oneByte = new byte[1];
            this.in = in;
        }
        
        @Override
        public void seek(final long p) throws IOException {
            if (p < 0L) {
                throw new IOException("Illegal seek: " + p);
            }
            this.in.seek(p);
        }
        
        @Override
        public long tell() throws IOException {
            return this.in.tell();
        }
        
        @Override
        public long length() throws IOException {
            return this.in.length();
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.in.read(b, 0, b.length);
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return this.in.read(b, off, len);
        }
        
        @Override
        public int read() throws IOException {
            final int n = this.read(this.oneByte, 0, 1);
            if (n == 1) {
                return this.oneByte[0] & 0xFF;
            }
            return n;
        }
        
        @Override
        public long skip(final long skip) throws IOException {
            final long position = this.in.tell();
            final long length = this.in.length();
            final long remaining = length - position;
            if (remaining > skip) {
                this.in.seek(skip);
                return this.in.tell() - position;
            }
            this.in.seek(remaining);
            return this.in.tell() - position;
        }
        
        @Override
        public void close() throws IOException {
            this.in.close();
            super.close();
        }
        
        @Override
        public int available() throws IOException {
            final long remaining = this.in.length() - this.in.tell();
            return (remaining > 2147483647L) ? Integer.MAX_VALUE : ((int)remaining);
        }
    }
}
