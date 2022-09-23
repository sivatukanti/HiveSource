// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UUEncoder
{
    protected static final int DEFAULT_MODE = 644;
    private static final int MAX_CHARS_PER_LINE = 45;
    private static final int INPUT_BUFFER_SIZE = 4500;
    private OutputStream out;
    private String name;
    
    public UUEncoder(final String name) {
        this.name = name;
    }
    
    public void encode(final InputStream is, final OutputStream out) throws IOException {
        this.out = out;
        this.encodeBegin();
        final byte[] buffer = new byte[4500];
        int count;
        while ((count = is.read(buffer, 0, buffer.length)) != -1) {
            int pos = 0;
            while (count > 0) {
                final int num = (count > 45) ? 45 : count;
                this.encodeLine(buffer, pos, num, out);
                pos += num;
                count -= num;
            }
        }
        out.flush();
        this.encodeEnd();
    }
    
    private void encodeString(final String n) throws IOException {
        final PrintStream writer = new PrintStream(this.out);
        writer.print(n);
        writer.flush();
    }
    
    private void encodeBegin() throws IOException {
        this.encodeString("begin 644 " + this.name + "\n");
    }
    
    private void encodeEnd() throws IOException {
        this.encodeString(" \nend\n");
    }
    
    private void encodeLine(final byte[] data, final int offset, final int length, final OutputStream out) throws IOException {
        out.write((byte)((length & 0x3F) + 32));
        int i = 0;
        while (i < length) {
            byte b = 1;
            byte c = 1;
            final byte a = data[offset + i++];
            if (i < length) {
                b = data[offset + i++];
                if (i < length) {
                    c = data[offset + i++];
                }
            }
            final byte d1 = (byte)((a >>> 2 & 0x3F) + 32);
            final byte d2 = (byte)(((a << 4 & 0x30) | (b >>> 4 & 0xF)) + 32);
            final byte d3 = (byte)(((b << 2 & 0x3C) | (c >>> 6 & 0x3)) + 32);
            final byte d4 = (byte)((c & 0x3F) + 32);
            out.write(d1);
            out.write(d2);
            out.write(d3);
            out.write(d4);
        }
        out.write(10);
    }
}
