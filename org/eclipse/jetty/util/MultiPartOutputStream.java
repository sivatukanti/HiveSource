// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class MultiPartOutputStream extends FilterOutputStream
{
    private static final byte[] __CRLF;
    private static final byte[] __DASHDASH;
    public static final String MULTIPART_MIXED = "multipart/mixed";
    public static final String MULTIPART_X_MIXED_REPLACE = "multipart/x-mixed-replace";
    private final String boundary;
    private final byte[] boundaryBytes;
    private boolean inPart;
    
    public MultiPartOutputStream(final OutputStream out) throws IOException {
        super(out);
        this.inPart = false;
        this.boundary = "jetty" + System.identityHashCode(this) + Long.toString(System.currentTimeMillis(), 36);
        this.boundaryBytes = this.boundary.getBytes(StandardCharsets.ISO_8859_1);
    }
    
    public MultiPartOutputStream(final OutputStream out, final String boundary) throws IOException {
        super(out);
        this.inPart = false;
        this.boundary = boundary;
        this.boundaryBytes = boundary.getBytes(StandardCharsets.ISO_8859_1);
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (this.inPart) {
                this.out.write(MultiPartOutputStream.__CRLF);
            }
            this.out.write(MultiPartOutputStream.__DASHDASH);
            this.out.write(this.boundaryBytes);
            this.out.write(MultiPartOutputStream.__DASHDASH);
            this.out.write(MultiPartOutputStream.__CRLF);
            this.inPart = false;
        }
        finally {
            super.close();
        }
    }
    
    public String getBoundary() {
        return this.boundary;
    }
    
    public OutputStream getOut() {
        return this.out;
    }
    
    public void startPart(final String contentType) throws IOException {
        if (this.inPart) {
            this.out.write(MultiPartOutputStream.__CRLF);
        }
        this.inPart = true;
        this.out.write(MultiPartOutputStream.__DASHDASH);
        this.out.write(this.boundaryBytes);
        this.out.write(MultiPartOutputStream.__CRLF);
        if (contentType != null) {
            this.out.write(("Content-Type: " + contentType).getBytes(StandardCharsets.ISO_8859_1));
        }
        this.out.write(MultiPartOutputStream.__CRLF);
        this.out.write(MultiPartOutputStream.__CRLF);
    }
    
    public void startPart(final String contentType, final String[] headers) throws IOException {
        if (this.inPart) {
            this.out.write(MultiPartOutputStream.__CRLF);
        }
        this.inPart = true;
        this.out.write(MultiPartOutputStream.__DASHDASH);
        this.out.write(this.boundaryBytes);
        this.out.write(MultiPartOutputStream.__CRLF);
        if (contentType != null) {
            this.out.write(("Content-Type: " + contentType).getBytes(StandardCharsets.ISO_8859_1));
        }
        this.out.write(MultiPartOutputStream.__CRLF);
        for (int i = 0; headers != null && i < headers.length; ++i) {
            this.out.write(headers[i].getBytes(StandardCharsets.ISO_8859_1));
            this.out.write(MultiPartOutputStream.__CRLF);
        }
        this.out.write(MultiPartOutputStream.__CRLF);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    static {
        __CRLF = new byte[] { 13, 10 };
        __DASHDASH = new byte[] { 45, 45 };
    }
}
