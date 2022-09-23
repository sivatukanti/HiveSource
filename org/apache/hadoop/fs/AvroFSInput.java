// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.avro.file.SeekableInput;
import java.io.Closeable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class AvroFSInput implements Closeable, SeekableInput
{
    private final FSDataInputStream stream;
    private final long len;
    
    public AvroFSInput(final FSDataInputStream in, final long len) {
        this.stream = in;
        this.len = len;
    }
    
    public AvroFSInput(final FileContext fc, final Path p) throws IOException {
        final FileStatus status = fc.getFileStatus(p);
        this.len = status.getLen();
        this.stream = fc.open(p);
    }
    
    @Override
    public long length() {
        return this.len;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.stream.read(b, off, len);
    }
    
    @Override
    public void seek(final long p) throws IOException {
        this.stream.seek(p);
    }
    
    @Override
    public long tell() throws IOException {
        return this.stream.getPos();
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
