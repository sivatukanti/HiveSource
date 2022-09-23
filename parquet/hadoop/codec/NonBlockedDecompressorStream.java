// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import java.io.IOException;
import org.apache.hadoop.io.compress.Decompressor;
import java.io.InputStream;
import org.apache.hadoop.io.compress.DecompressorStream;

public class NonBlockedDecompressorStream extends DecompressorStream
{
    private boolean inputHandled;
    
    public NonBlockedDecompressorStream(final InputStream stream, final Decompressor decompressor, final int bufferSize) throws IOException {
        super(stream, decompressor, bufferSize);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (!this.inputHandled) {
            while (true) {
                final int compressedBytes = this.getCompressedData();
                if (compressedBytes == -1) {
                    break;
                }
                this.decompressor.setInput(this.buffer, 0, compressedBytes);
            }
            this.inputHandled = true;
        }
        final int decompressedBytes = this.decompressor.decompress(b, off, len);
        if (this.decompressor.finished()) {
            this.decompressor.reset();
        }
        return decompressedBytes;
    }
}
