// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import org.apache.tools.ant.types.ResourceCollection;

public class GZipResource extends CompressedResource
{
    public GZipResource() {
    }
    
    public GZipResource(final ResourceCollection other) {
        super(other);
    }
    
    @Override
    protected InputStream wrapStream(final InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }
    
    @Override
    protected OutputStream wrapStream(final OutputStream out) throws IOException {
        return new GZIPOutputStream(out);
    }
    
    @Override
    protected String getCompressionName() {
        return "GZip";
    }
}
