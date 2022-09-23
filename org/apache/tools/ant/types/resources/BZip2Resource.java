// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.bzip2.CBZip2OutputStream;
import java.io.OutputStream;
import org.apache.tools.bzip2.CBZip2InputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.types.ResourceCollection;

public class BZip2Resource extends CompressedResource
{
    private static final char[] MAGIC;
    
    public BZip2Resource() {
    }
    
    public BZip2Resource(final ResourceCollection other) {
        super(other);
    }
    
    @Override
    protected InputStream wrapStream(final InputStream in) throws IOException {
        for (int i = 0; i < BZip2Resource.MAGIC.length; ++i) {
            if (in.read() != BZip2Resource.MAGIC[i]) {
                throw new IOException("Invalid bz2 stream.");
            }
        }
        return new CBZip2InputStream(in);
    }
    
    @Override
    protected OutputStream wrapStream(final OutputStream out) throws IOException {
        for (int i = 0; i < BZip2Resource.MAGIC.length; ++i) {
            out.write(BZip2Resource.MAGIC[i]);
        }
        return new CBZip2OutputStream(out);
    }
    
    @Override
    protected String getCompressionName() {
        return "Bzip2";
    }
    
    static {
        MAGIC = new char[] { 'B', 'Z' };
    }
}
