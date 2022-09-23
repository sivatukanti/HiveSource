// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class MultiPartOutputStream extends FilterOutputStream
{
    private static byte[] __CRLF;
    private static byte[] __DASHDASH;
    public static String MULTIPART_MIXED;
    public static String MULTIPART_X_MIXED_REPLACE;
    private String boundary;
    private byte[] boundaryBytes;
    private boolean inPart;
    
    public MultiPartOutputStream(final OutputStream out) throws IOException {
        super(out);
        this.inPart = false;
        this.boundary = "jetty" + System.identityHashCode(this) + Long.toString(System.currentTimeMillis(), 36);
        this.boundaryBytes = this.boundary.getBytes(StringUtil.__ISO_8859_1);
        this.inPart = false;
    }
    
    public void close() throws IOException {
        if (this.inPart) {
            this.out.write(MultiPartOutputStream.__CRLF);
        }
        this.out.write(MultiPartOutputStream.__DASHDASH);
        this.out.write(this.boundaryBytes);
        this.out.write(MultiPartOutputStream.__DASHDASH);
        this.out.write(MultiPartOutputStream.__CRLF);
        this.inPart = false;
        super.close();
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
        this.out.write(("Content-Type: " + contentType).getBytes(StringUtil.__ISO_8859_1));
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
        this.out.write(("Content-Type: " + contentType).getBytes(StringUtil.__ISO_8859_1));
        this.out.write(MultiPartOutputStream.__CRLF);
        for (int i = 0; headers != null && i < headers.length; ++i) {
            this.out.write(headers[i].getBytes(StringUtil.__ISO_8859_1));
            this.out.write(MultiPartOutputStream.__CRLF);
        }
        this.out.write(MultiPartOutputStream.__CRLF);
    }
    
    static {
        MultiPartOutputStream.MULTIPART_MIXED = "multipart/mixed";
        MultiPartOutputStream.MULTIPART_X_MIXED_REPLACE = "multipart/x-mixed-replace";
        try {
            MultiPartOutputStream.__CRLF = "\r\n".getBytes(StringUtil.__ISO_8859_1);
            MultiPartOutputStream.__DASHDASH = "--".getBytes(StringUtil.__ISO_8859_1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
