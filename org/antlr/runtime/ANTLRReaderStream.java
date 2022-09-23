// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.io.IOException;
import java.io.Reader;

public class ANTLRReaderStream extends ANTLRStringStream
{
    public static final int READ_BUFFER_SIZE = 1024;
    public static final int INITIAL_BUFFER_SIZE = 1024;
    
    public ANTLRReaderStream() {
    }
    
    public ANTLRReaderStream(final Reader r) throws IOException {
        this(r, 1024, 1024);
    }
    
    public ANTLRReaderStream(final Reader r, final int size) throws IOException {
        this(r, size, 1024);
    }
    
    public ANTLRReaderStream(final Reader r, final int size, final int readChunkSize) throws IOException {
        this.load(r, size, readChunkSize);
    }
    
    public void load(final Reader r, int size, int readChunkSize) throws IOException {
        if (r == null) {
            return;
        }
        if (size <= 0) {
            size = 1024;
        }
        if (readChunkSize <= 0) {
            readChunkSize = 1024;
        }
        try {
            this.data = new char[size];
            int numRead = 0;
            int p = 0;
            do {
                if (p + readChunkSize > this.data.length) {
                    final char[] newdata = new char[this.data.length * 2];
                    System.arraycopy(this.data, 0, newdata, 0, this.data.length);
                    this.data = newdata;
                }
                numRead = r.read(this.data, p, readChunkSize);
                p += numRead;
            } while (numRead != -1);
            super.n = p + 1;
        }
        finally {
            r.close();
        }
    }
}
