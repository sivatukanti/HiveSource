// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.io.FileOutputStream;
import org.jboss.netty.handler.codec.http.HttpConstants;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.File;
import org.jboss.netty.buffer.ChannelBuffers;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.jboss.netty.buffer.ChannelBuffer;

public abstract class AbstractMemoryHttpData extends AbstractHttpData
{
    private ChannelBuffer channelBuffer;
    private int chunkPosition;
    protected boolean isRenamed;
    
    protected AbstractMemoryHttpData(final String name, final Charset charset, final long size) {
        super(name, charset, size);
    }
    
    public void setContent(final ChannelBuffer buffer) throws IOException {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        final long localsize = buffer.readableBytes();
        this.checkSize(localsize);
        if (this.definedSize > 0L && this.definedSize < localsize) {
            throw new IOException("Out of size: " + localsize + " > " + this.definedSize);
        }
        this.channelBuffer = buffer;
        this.size = localsize;
        this.completed = true;
    }
    
    public void setContent(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }
        final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        final byte[] bytes = new byte[16384];
        int read = inputStream.read(bytes);
        int written = 0;
        while (read > 0) {
            buffer.writeBytes(bytes, 0, read);
            written += read;
            this.checkSize(written);
            read = inputStream.read(bytes);
        }
        this.size = written;
        if (this.definedSize > 0L && this.definedSize < this.size) {
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
        }
        this.channelBuffer = buffer;
        this.completed = true;
    }
    
    public void addContent(final ChannelBuffer buffer, final boolean last) throws IOException {
        if (buffer != null) {
            final long localsize = buffer.readableBytes();
            this.checkSize(this.size + localsize);
            if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
                throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
            }
            this.size += localsize;
            if (this.channelBuffer == null) {
                this.channelBuffer = buffer;
            }
            else {
                this.channelBuffer = ChannelBuffers.wrappedBuffer(this.channelBuffer, buffer);
            }
        }
        if (last) {
            this.completed = true;
        }
        else if (buffer == null) {
            throw new NullPointerException("buffer");
        }
    }
    
    public void setContent(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file");
        }
        final long newsize = file.length();
        if (newsize > 2147483647L) {
            throw new IllegalArgumentException("File too big to be loaded in memory");
        }
        this.checkSize(newsize);
        final FileInputStream inputStream = new FileInputStream(file);
        final FileChannel fileChannel = inputStream.getChannel();
        final byte[] array = new byte[(int)newsize];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        for (int read = 0; read < newsize; read += fileChannel.read(byteBuffer)) {}
        fileChannel.close();
        inputStream.close();
        byteBuffer.flip();
        this.channelBuffer = ChannelBuffers.wrappedBuffer(byteBuffer);
        this.size = newsize;
        this.completed = true;
    }
    
    public void delete() {
    }
    
    public byte[] get() {
        if (this.channelBuffer == null) {
            return new byte[0];
        }
        final byte[] array = new byte[this.channelBuffer.readableBytes()];
        this.channelBuffer.getBytes(this.channelBuffer.readerIndex(), array);
        return array;
    }
    
    public String getString() {
        return this.getString(HttpConstants.DEFAULT_CHARSET);
    }
    
    public String getString(Charset encoding) {
        if (this.channelBuffer == null) {
            return "";
        }
        if (encoding == null) {
            encoding = HttpConstants.DEFAULT_CHARSET;
        }
        return this.channelBuffer.toString(encoding);
    }
    
    public ChannelBuffer getChannelBuffer() {
        return this.channelBuffer;
    }
    
    public ChannelBuffer getChunk(final int length) throws IOException {
        if (this.channelBuffer == null || length == 0 || this.channelBuffer.readableBytes() == 0) {
            this.chunkPosition = 0;
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final int sizeLeft = this.channelBuffer.readableBytes() - this.chunkPosition;
        if (sizeLeft == 0) {
            this.chunkPosition = 0;
            return ChannelBuffers.EMPTY_BUFFER;
        }
        int sliceLength;
        if (sizeLeft < (sliceLength = length)) {
            sliceLength = sizeLeft;
        }
        final ChannelBuffer chunk = this.channelBuffer.slice(this.chunkPosition, sliceLength);
        this.chunkPosition += sliceLength;
        return chunk;
    }
    
    public boolean isInMemory() {
        return true;
    }
    
    public boolean renameTo(final File dest) throws IOException {
        if (dest == null) {
            throw new NullPointerException("dest");
        }
        if (this.channelBuffer == null) {
            dest.createNewFile();
            return this.isRenamed = true;
        }
        final int length = this.channelBuffer.readableBytes();
        final FileOutputStream outputStream = new FileOutputStream(dest);
        FileChannel fileChannel;
        ByteBuffer byteBuffer;
        int written;
        for (fileChannel = outputStream.getChannel(), byteBuffer = this.channelBuffer.toByteBuffer(), written = 0; written < length; written += fileChannel.write(byteBuffer)) {}
        fileChannel.force(false);
        fileChannel.close();
        outputStream.close();
        this.isRenamed = true;
        return written == length;
    }
    
    public File getFile() throws IOException {
        throw new IOException("Not represented by a file");
    }
}
