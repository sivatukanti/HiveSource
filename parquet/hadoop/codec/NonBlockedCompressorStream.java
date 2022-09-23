// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import java.io.IOException;
import org.apache.hadoop.io.compress.Compressor;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.CompressorStream;

public class NonBlockedCompressorStream extends CompressorStream
{
    public NonBlockedCompressorStream(final OutputStream stream, final Compressor compressor, final int bufferSize) {
        super(stream, compressor, bufferSize);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.compressor.finished()) {
            throw new IOException("write beyond end of stream");
        }
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.compressor.setInput(b, off, len);
    }
}
