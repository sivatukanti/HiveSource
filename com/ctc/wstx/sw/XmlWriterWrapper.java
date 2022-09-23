// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import java.io.IOException;
import java.io.Writer;

public abstract class XmlWriterWrapper extends Writer
{
    protected final XmlWriter mWriter;
    private char[] mBuffer;
    
    public static XmlWriterWrapper wrapWriteRaw(final XmlWriter xw) {
        return new RawWrapper(xw);
    }
    
    public static XmlWriterWrapper wrapWriteCharacters(final XmlWriter xw) {
        return new TextWrapper(xw);
    }
    
    protected XmlWriterWrapper(final XmlWriter writer) {
        this.mBuffer = null;
        this.mWriter = writer;
    }
    
    @Override
    public final void close() throws IOException {
        this.mWriter.close(false);
    }
    
    @Override
    public final void flush() throws IOException {
        this.mWriter.flush();
    }
    
    @Override
    public final void write(final char[] cbuf) throws IOException {
        this.write(cbuf, 0, cbuf.length);
    }
    
    @Override
    public abstract void write(final char[] p0, final int p1, final int p2) throws IOException;
    
    @Override
    public final void write(final int c) throws IOException {
        if (this.mBuffer == null) {
            this.mBuffer = new char[1];
        }
        this.mBuffer[0] = (char)c;
        this.write(this.mBuffer, 0, 1);
    }
    
    @Override
    public abstract void write(final String p0) throws IOException;
    
    @Override
    public abstract void write(final String p0, final int p1, final int p2) throws IOException;
    
    private static final class RawWrapper extends XmlWriterWrapper
    {
        protected RawWrapper(final XmlWriter writer) {
            super(writer);
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            this.mWriter.writeRaw(cbuf, off, len);
        }
        
        @Override
        public void write(final String str, final int off, final int len) throws IOException {
            this.mWriter.writeRaw(str, off, len);
        }
        
        @Override
        public final void write(final String str) throws IOException {
            this.mWriter.writeRaw(str, 0, str.length());
        }
    }
    
    private static class TextWrapper extends XmlWriterWrapper
    {
        protected TextWrapper(final XmlWriter writer) {
            super(writer);
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            this.mWriter.writeCharacters(cbuf, off, len);
        }
        
        @Override
        public void write(final String str) throws IOException {
            this.mWriter.writeCharacters(str);
        }
        
        @Override
        public void write(final String str, final int off, final int len) throws IOException {
            this.mWriter.writeCharacters(str.substring(off, off + len));
        }
    }
}
