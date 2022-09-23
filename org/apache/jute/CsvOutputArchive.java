// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.util.TreeMap;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;

public class CsvOutputArchive implements OutputArchive
{
    private PrintStream stream;
    private boolean isFirst;
    
    static CsvOutputArchive getArchive(final OutputStream strm) throws UnsupportedEncodingException {
        return new CsvOutputArchive(strm);
    }
    
    private void throwExceptionOnError(final String tag) throws IOException {
        if (this.stream.checkError()) {
            throw new IOException("Error serializing " + tag);
        }
    }
    
    private void printCommaUnlessFirst() {
        if (!this.isFirst) {
            this.stream.print(",");
        }
        this.isFirst = false;
    }
    
    public CsvOutputArchive(final OutputStream out) throws UnsupportedEncodingException {
        this.isFirst = true;
        this.stream = new PrintStream(out, true, "UTF-8");
    }
    
    @Override
    public void writeByte(final byte b, final String tag) throws IOException {
        this.writeLong(b, tag);
    }
    
    @Override
    public void writeBool(final boolean b, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        final String val = b ? "T" : "F";
        this.stream.print(val);
        this.throwExceptionOnError(tag);
    }
    
    @Override
    public void writeInt(final int i, final String tag) throws IOException {
        this.writeLong(i, tag);
    }
    
    @Override
    public void writeLong(final long l, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        this.stream.print(l);
        this.throwExceptionOnError(tag);
    }
    
    @Override
    public void writeFloat(final float f, final String tag) throws IOException {
        this.writeDouble(f, tag);
    }
    
    @Override
    public void writeDouble(final double d, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        this.stream.print(d);
        this.throwExceptionOnError(tag);
    }
    
    @Override
    public void writeString(final String s, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        this.stream.print(Utils.toCSVString(s));
        this.throwExceptionOnError(tag);
    }
    
    @Override
    public void writeBuffer(final byte[] buf, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        this.stream.print(Utils.toCSVBuffer(buf));
        this.throwExceptionOnError(tag);
    }
    
    @Override
    public void writeRecord(final Record r, final String tag) throws IOException {
        if (r == null) {
            return;
        }
        r.serialize(this, tag);
    }
    
    @Override
    public void startRecord(final Record r, final String tag) throws IOException {
        if (tag != null && !"".equals(tag)) {
            this.printCommaUnlessFirst();
            this.stream.print("s{");
            this.isFirst = true;
        }
    }
    
    @Override
    public void endRecord(final Record r, final String tag) throws IOException {
        if (tag == null || "".equals(tag)) {
            this.stream.print("\n");
            this.isFirst = true;
        }
        else {
            this.stream.print("}");
            this.isFirst = false;
        }
    }
    
    @Override
    public void startVector(final List v, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        this.stream.print("v{");
        this.isFirst = true;
    }
    
    @Override
    public void endVector(final List v, final String tag) throws IOException {
        this.stream.print("}");
        this.isFirst = false;
    }
    
    @Override
    public void startMap(final TreeMap v, final String tag) throws IOException {
        this.printCommaUnlessFirst();
        this.stream.print("m{");
        this.isFirst = true;
    }
    
    @Override
    public void endMap(final TreeMap v, final String tag) throws IOException {
        this.stream.print("}");
        this.isFirst = false;
    }
}
