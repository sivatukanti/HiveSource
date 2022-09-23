// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.io.InputStream;
import java.io.File;
import org.jboss.netty.buffer.ChannelBuffer;
import java.io.IOException;
import java.nio.charset.Charset;

public class MixedFileUpload implements FileUpload
{
    private FileUpload fileUpload;
    private final long limitSize;
    private final long definedSize;
    protected long maxSize;
    
    public MixedFileUpload(final String name, final String filename, final String contentType, final String contentTransferEncoding, final Charset charset, final long size, final long limitSize) {
        this.maxSize = -1L;
        this.limitSize = limitSize;
        if (size > this.limitSize) {
            this.fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
        }
        else {
            this.fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
        }
        this.definedSize = size;
    }
    
    public void setMaxSize(final long maxSize) {
        this.maxSize = maxSize;
        this.fileUpload.setMaxSize(maxSize);
    }
    
    public void checkSize(final long newSize) throws IOException {
        if (this.maxSize >= 0L && newSize > this.maxSize) {
            throw new IOException("Size exceed allowed maximum capacity");
        }
    }
    
    public void addContent(final ChannelBuffer buffer, final boolean last) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload) {
            this.checkSize(this.fileUpload.length() + buffer.readableBytes());
            if (this.fileUpload.length() + buffer.readableBytes() > this.limitSize) {
                final DiskFileUpload diskFileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
                diskFileUpload.setMaxSize(this.maxSize);
                if (((MemoryFileUpload)this.fileUpload).getChannelBuffer() != null) {
                    diskFileUpload.addContent(((MemoryFileUpload)this.fileUpload).getChannelBuffer(), false);
                }
                this.fileUpload = diskFileUpload;
            }
        }
        this.fileUpload.addContent(buffer, last);
    }
    
    public void delete() {
        this.fileUpload.delete();
    }
    
    public byte[] get() throws IOException {
        return this.fileUpload.get();
    }
    
    public ChannelBuffer getChannelBuffer() throws IOException {
        return this.fileUpload.getChannelBuffer();
    }
    
    public Charset getCharset() {
        return this.fileUpload.getCharset();
    }
    
    public String getContentType() {
        return this.fileUpload.getContentType();
    }
    
    public String getContentTransferEncoding() {
        return this.fileUpload.getContentTransferEncoding();
    }
    
    public String getFilename() {
        return this.fileUpload.getFilename();
    }
    
    public String getString() throws IOException {
        return this.fileUpload.getString();
    }
    
    public String getString(final Charset encoding) throws IOException {
        return this.fileUpload.getString(encoding);
    }
    
    public boolean isCompleted() {
        return this.fileUpload.isCompleted();
    }
    
    public boolean isInMemory() {
        return this.fileUpload.isInMemory();
    }
    
    public long length() {
        return this.fileUpload.length();
    }
    
    public boolean renameTo(final File dest) throws IOException {
        return this.fileUpload.renameTo(dest);
    }
    
    public void setCharset(final Charset charset) {
        this.fileUpload.setCharset(charset);
    }
    
    public void setContent(final ChannelBuffer buffer) throws IOException {
        this.checkSize(buffer.readableBytes());
        if (buffer.readableBytes() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            (this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize)).setMaxSize(this.maxSize);
        }
        this.fileUpload.setContent(buffer);
    }
    
    public void setContent(final File file) throws IOException {
        this.checkSize(file.length());
        if (file.length() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            (this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize)).setMaxSize(this.maxSize);
        }
        this.fileUpload.setContent(file);
    }
    
    public void setContent(final InputStream inputStream) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload) {
            (this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize)).setMaxSize(this.maxSize);
        }
        this.fileUpload.setContent(inputStream);
    }
    
    public void setContentType(final String contentType) {
        this.fileUpload.setContentType(contentType);
    }
    
    public void setContentTransferEncoding(final String contentTransferEncoding) {
        this.fileUpload.setContentTransferEncoding(contentTransferEncoding);
    }
    
    public void setFilename(final String filename) {
        this.fileUpload.setFilename(filename);
    }
    
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.fileUpload.getHttpDataType();
    }
    
    public String getName() {
        return this.fileUpload.getName();
    }
    
    public int compareTo(final InterfaceHttpData o) {
        return this.fileUpload.compareTo(o);
    }
    
    @Override
    public String toString() {
        return "Mixed: " + this.fileUpload.toString();
    }
    
    public ChannelBuffer getChunk(final int length) throws IOException {
        return this.fileUpload.getChunk(length);
    }
    
    public File getFile() throws IOException {
        return this.fileUpload.getFile();
    }
}
