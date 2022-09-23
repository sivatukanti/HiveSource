// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;
import java.io.PushbackReader;

class CsvInputArchive implements InputArchive
{
    private PushbackReader stream;
    
    private void throwExceptionOnError(final String tag) throws IOException {
        throw new IOException("Error deserializing " + tag);
    }
    
    private String readField(final String tag) throws IOException {
        try {
            final StringBuilder buf = new StringBuilder();
            while (true) {
                final char c = (char)this.stream.read();
                switch (c) {
                    case ',': {
                        return buf.toString();
                    }
                    case '\n':
                    case '\r':
                    case '}': {
                        this.stream.unread(c);
                        return buf.toString();
                    }
                    default: {
                        buf.append(c);
                        continue;
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new IOException("Error reading " + tag);
        }
    }
    
    static CsvInputArchive getArchive(final InputStream strm) throws UnsupportedEncodingException {
        return new CsvInputArchive(strm);
    }
    
    public CsvInputArchive(final InputStream in) throws UnsupportedEncodingException {
        this.stream = new PushbackReader(new InputStreamReader(in, "UTF-8"));
    }
    
    @Override
    public byte readByte(final String tag) throws IOException {
        return (byte)this.readLong(tag);
    }
    
    @Override
    public boolean readBool(final String tag) throws IOException {
        final String sval = this.readField(tag);
        return "T".equals(sval);
    }
    
    @Override
    public int readInt(final String tag) throws IOException {
        return (int)this.readLong(tag);
    }
    
    @Override
    public long readLong(final String tag) throws IOException {
        final String sval = this.readField(tag);
        try {
            final long lval = Long.parseLong(sval);
            return lval;
        }
        catch (NumberFormatException ex) {
            throw new IOException("Error deserializing " + tag);
        }
    }
    
    @Override
    public float readFloat(final String tag) throws IOException {
        return (float)this.readDouble(tag);
    }
    
    @Override
    public double readDouble(final String tag) throws IOException {
        final String sval = this.readField(tag);
        try {
            final double dval = Double.parseDouble(sval);
            return dval;
        }
        catch (NumberFormatException ex) {
            throw new IOException("Error deserializing " + tag);
        }
    }
    
    @Override
    public String readString(final String tag) throws IOException {
        final String sval = this.readField(tag);
        return Utils.fromCSVString(sval);
    }
    
    @Override
    public byte[] readBuffer(final String tag) throws IOException {
        final String sval = this.readField(tag);
        return Utils.fromCSVBuffer(sval);
    }
    
    @Override
    public void readRecord(final Record r, final String tag) throws IOException {
        r.deserialize(this, tag);
    }
    
    @Override
    public void startRecord(final String tag) throws IOException {
        if (tag != null && !"".equals(tag)) {
            final char c1 = (char)this.stream.read();
            final char c2 = (char)this.stream.read();
            if (c1 != 's' || c2 != '{') {
                throw new IOException("Error deserializing " + tag);
            }
        }
    }
    
    @Override
    public void endRecord(final String tag) throws IOException {
        char c = (char)this.stream.read();
        if (tag == null || "".equals(tag)) {
            if (c != '\n' && c != '\r') {
                throw new IOException("Error deserializing record.");
            }
        }
        else {
            if (c != '}') {
                throw new IOException("Error deserializing " + tag);
            }
            c = (char)this.stream.read();
            if (c != ',') {
                this.stream.unread(c);
            }
        }
    }
    
    @Override
    public Index startVector(final String tag) throws IOException {
        final char c1 = (char)this.stream.read();
        final char c2 = (char)this.stream.read();
        if (c1 != 'v' || c2 != '{') {
            throw new IOException("Error deserializing " + tag);
        }
        return new CsvIndex();
    }
    
    @Override
    public void endVector(final String tag) throws IOException {
        char c = (char)this.stream.read();
        if (c != '}') {
            throw new IOException("Error deserializing " + tag);
        }
        c = (char)this.stream.read();
        if (c != ',') {
            this.stream.unread(c);
        }
    }
    
    @Override
    public Index startMap(final String tag) throws IOException {
        final char c1 = (char)this.stream.read();
        final char c2 = (char)this.stream.read();
        if (c1 != 'm' || c2 != '{') {
            throw new IOException("Error deserializing " + tag);
        }
        return new CsvIndex();
    }
    
    @Override
    public void endMap(final String tag) throws IOException {
        char c = (char)this.stream.read();
        if (c != '}') {
            throw new IOException("Error deserializing " + tag);
        }
        c = (char)this.stream.read();
        if (c != ',') {
            this.stream.unread(c);
        }
    }
    
    private class CsvIndex implements Index
    {
        @Override
        public boolean done() {
            char c = '\0';
            try {
                c = (char)CsvInputArchive.this.stream.read();
                CsvInputArchive.this.stream.unread(c);
            }
            catch (IOException ex) {}
            return c == '}';
        }
        
        @Override
        public void incr() {
        }
    }
}
