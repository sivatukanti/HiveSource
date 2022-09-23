// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.io.File;
import java.util.UUID;
import java.io.EOFException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileOutputStream;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ObjectWriter;
import org.apache.htrace.shaded.commons.logging.Log;

public class LocalFileSpanReceiver extends SpanReceiver
{
    private static final Log LOG;
    public static final String PATH_KEY = "local.file.span.receiver.path";
    public static final String CAPACITY_KEY = "local.file.span.receiver.capacity";
    public static final int CAPACITY_DEFAULT = 5000;
    private static ObjectWriter JSON_WRITER;
    private final String path;
    private byte[][] bufferedSpans;
    private int bufferedSpansIndex;
    private final ReentrantLock bufferLock;
    private final FileOutputStream stream;
    private final FileChannel channel;
    private final ReentrantLock channelLock;
    private final int WRITEV_SIZE = 20;
    private static final ByteBuffer newlineBuf;
    
    public LocalFileSpanReceiver(final HTraceConfiguration conf) {
        this.bufferLock = new ReentrantLock();
        this.channelLock = new ReentrantLock();
        final int capacity = conf.getInt("local.file.span.receiver.capacity", 5000);
        if (capacity < 1) {
            throw new IllegalArgumentException("local.file.span.receiver.capacity must not be less than 1.");
        }
        final String pathStr = conf.get("local.file.span.receiver.path");
        if (pathStr == null || pathStr.isEmpty()) {
            this.path = getUniqueLocalTraceFileName();
        }
        else {
            this.path = pathStr;
        }
        final boolean success = false;
        try {
            this.stream = new FileOutputStream(this.path, true);
        }
        catch (IOException ioe) {
            LocalFileSpanReceiver.LOG.error("Error opening " + this.path + ": " + ioe.getMessage());
            throw new RuntimeException(ioe);
        }
        this.channel = this.stream.getChannel();
        if (this.channel == null) {
            try {
                this.stream.close();
            }
            catch (IOException e) {
                LocalFileSpanReceiver.LOG.error("Error closing " + this.path, e);
            }
            LocalFileSpanReceiver.LOG.error("Failed to get channel for " + this.path);
            throw new RuntimeException("Failed to get channel for " + this.path);
        }
        this.bufferedSpans = new byte[capacity][];
        this.bufferedSpansIndex = 0;
        if (LocalFileSpanReceiver.LOG.isDebugEnabled()) {
            LocalFileSpanReceiver.LOG.debug("Created new LocalFileSpanReceiver with path = " + this.path + ", capacity = " + capacity);
        }
    }
    
    private void doFlush(final byte[][] toFlush, final int len) throws IOException {
        int bidx = 0;
        int widx = 0;
        final ByteBuffer[] writevBufs = new ByteBuffer[40];
        while (true) {
            if (widx == writevBufs.length) {
                this.channel.write(writevBufs);
                widx = 0;
            }
            if (bidx == len) {
                break;
            }
            writevBufs[widx] = ByteBuffer.wrap(toFlush[bidx]);
            writevBufs[widx + 1] = LocalFileSpanReceiver.newlineBuf;
            ++bidx;
            widx += 2;
        }
        if (widx > 0) {
            this.channel.write(writevBufs, 0, widx);
        }
    }
    
    @Override
    public void receiveSpan(final Span span) {
        byte[] jsonBuf = null;
        try {
            jsonBuf = LocalFileSpanReceiver.JSON_WRITER.writeValueAsBytes(span);
        }
        catch (JsonProcessingException e) {
            LocalFileSpanReceiver.LOG.error("receiveSpan(path=" + this.path + ", span=" + span + "): " + "Json processing error: " + e.getMessage());
            return;
        }
        byte[][] toFlush = null;
        this.bufferLock.lock();
        try {
            if (this.bufferedSpans == null) {
                LocalFileSpanReceiver.LOG.debug("receiveSpan(path=" + this.path + ", span=" + span + "): " + "LocalFileSpanReceiver for " + this.path + " is closed.");
                return;
            }
            this.bufferedSpans[this.bufferedSpansIndex] = jsonBuf;
            ++this.bufferedSpansIndex;
            if (this.bufferedSpansIndex == this.bufferedSpans.length) {
                toFlush = this.bufferedSpans;
                this.bufferedSpansIndex = 0;
                this.bufferedSpans = new byte[this.bufferedSpans.length][];
            }
        }
        finally {
            this.bufferLock.unlock();
        }
        if (toFlush != null) {
            this.channelLock.lock();
            try {
                this.doFlush(toFlush, toFlush.length);
            }
            catch (IOException ioe) {
                LocalFileSpanReceiver.LOG.error("Error flushing buffers to " + this.path + ": " + ioe.getMessage());
            }
            finally {
                this.channelLock.unlock();
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        byte[][] toFlush = null;
        int numToFlush = 0;
        this.bufferLock.lock();
        try {
            if (this.bufferedSpans == null) {
                LocalFileSpanReceiver.LOG.info("LocalFileSpanReceiver for " + this.path + " was already closed.");
                return;
            }
            numToFlush = this.bufferedSpansIndex;
            this.bufferedSpansIndex = 0;
            toFlush = this.bufferedSpans;
            this.bufferedSpans = null;
        }
        finally {
            this.bufferLock.unlock();
        }
        this.channelLock.lock();
        try {
            this.doFlush(toFlush, numToFlush);
        }
        catch (IOException ioe) {
            LocalFileSpanReceiver.LOG.error("Error flushing buffers to " + this.path + ": " + ioe.getMessage());
        }
        finally {
            try {
                this.stream.close();
            }
            catch (IOException e) {
                LocalFileSpanReceiver.LOG.error("Error closing stream for " + this.path, e);
            }
            this.channelLock.unlock();
        }
    }
    
    public static String getUniqueLocalTraceFileName() {
        final String tmp = System.getProperty("java.io.tmpdir", "/tmp");
        String nonce = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/self/stat"), "UTF-8"));
            final String line = reader.readLine();
            if (line == null) {
                throw new EOFException();
            }
            nonce = line.split(" ")[0];
        }
        catch (IOException e) {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    LocalFileSpanReceiver.LOG.warn("Exception in closing " + reader, e);
                }
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e2) {
                    LocalFileSpanReceiver.LOG.warn("Exception in closing " + reader, e2);
                }
            }
        }
        if (nonce == null) {
            nonce = UUID.randomUUID().toString();
        }
        return new File(tmp, nonce).getAbsolutePath();
    }
    
    static {
        LOG = LogFactory.getLog(LocalFileSpanReceiver.class);
        LocalFileSpanReceiver.JSON_WRITER = new ObjectMapper().writer();
        newlineBuf = ByteBuffer.wrap(new byte[] { 10 });
    }
}
