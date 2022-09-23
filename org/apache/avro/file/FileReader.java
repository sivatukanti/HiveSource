// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import org.apache.avro.Schema;
import java.io.Closeable;
import java.util.Iterator;

public interface FileReader<D> extends Iterator<D>, Iterable<D>, Closeable
{
    Schema getSchema();
    
    D next(final D p0) throws IOException;
    
    void sync(final long p0) throws IOException;
    
    boolean pastSync(final long p0) throws IOException;
    
    long tell() throws IOException;
}
