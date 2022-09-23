// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zlib;

import java.util.zip.DataFormatException;
import java.io.IOException;
import org.apache.hadoop.io.compress.Decompressor;
import java.util.zip.Inflater;

public class BuiltInZlibInflater extends Inflater implements Decompressor
{
    public BuiltInZlibInflater(final boolean nowrap) {
        super(nowrap);
    }
    
    public BuiltInZlibInflater() {
    }
    
    @Override
    public synchronized int decompress(final byte[] b, final int off, final int len) throws IOException {
        try {
            return super.inflate(b, off, len);
        }
        catch (DataFormatException dfe) {
            throw new IOException(dfe.getMessage());
        }
    }
}
