// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.io;

import java.io.IOException;
import javax.annotation.Nullable;
import java.io.OutputStream;
import org.apache.curator.shaded.com.google.common.annotations.Beta;
import java.io.FilterOutputStream;

@Beta
public final class CountingOutputStream extends FilterOutputStream
{
    private long count;
    
    public CountingOutputStream(@Nullable final OutputStream out) {
        super(out);
    }
    
    public long getCount() {
        return this.count;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
        this.count += len;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
        ++this.count;
    }
}
