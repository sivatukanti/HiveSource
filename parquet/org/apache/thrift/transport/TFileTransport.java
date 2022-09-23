// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import java.io.BufferedInputStream;
import java.util.Random;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TFileTransport extends TTransport
{
    tailPolicy currentPolicy_;
    protected TSeekableFile inputFile_;
    protected OutputStream outputStream_;
    Event currentEvent_;
    InputStream inputStream_;
    chunkState cs;
    private int readTimeout_;
    private boolean readOnly_;
    
    public tailPolicy getTailPolicy() {
        return this.currentPolicy_;
    }
    
    public tailPolicy setTailPolicy(final tailPolicy policy) {
        final tailPolicy old = this.currentPolicy_;
        this.currentPolicy_ = policy;
        return old;
    }
    
    private InputStream createInputStream() throws TTransportException {
        InputStream is;
        try {
            if (this.inputStream_ != null) {
                ((truncableBufferedInputStream)this.inputStream_).trunc();
                is = this.inputStream_;
            }
            else {
                is = new truncableBufferedInputStream(this.inputFile_.getInputStream());
            }
        }
        catch (IOException iox) {
            System.err.println("createInputStream: " + iox.getMessage());
            throw new TTransportException(iox.getMessage(), iox);
        }
        return is;
    }
    
    private int tailRead(final InputStream is, final byte[] buf, int off, int len, final tailPolicy tp) throws TTransportException {
        final int orig_len = len;
        try {
            int retries = 0;
            while (len > 0) {
                final int cnt = is.read(buf, off, len);
                if (cnt > 0) {
                    off += cnt;
                    len -= cnt;
                    retries = 0;
                    this.cs.skip(cnt);
                }
                else {
                    if (cnt != -1) {
                        throw new TTransportException("Unexpected return from InputStream.read = " + cnt);
                    }
                    ++retries;
                    if (tp.retries_ != -1 && tp.retries_ < retries) {
                        return orig_len - len;
                    }
                    if (tp.timeout_ <= 0) {
                        continue;
                    }
                    try {
                        Thread.sleep(tp.timeout_);
                    }
                    catch (InterruptedException e) {}
                }
            }
        }
        catch (IOException iox) {
            throw new TTransportException(iox.getMessage(), iox);
        }
        return orig_len - len;
    }
    
    private boolean performRecovery() throws TTransportException {
        final int numChunks = this.getNumChunks();
        final int curChunk = this.cs.getChunkNum();
        if (curChunk >= numChunks - 1) {
            return false;
        }
        this.seekToChunk(curChunk + 1);
        return true;
    }
    
    private boolean readEvent() throws TTransportException {
        final byte[] ebytes = new byte[4];
        int esize;
        do {
            final int nrequested = this.cs.getRemaining();
            if (nrequested < 4) {
                final int nread = this.tailRead(this.inputStream_, ebytes, 0, nrequested, this.currentPolicy_);
                if (nread != nrequested) {
                    return false;
                }
            }
            final int nread = this.tailRead(this.inputStream_, ebytes, 0, 4, this.currentPolicy_);
            if (nread != 4) {
                return false;
            }
            esize = 0;
            for (int i = 3; i >= 0; --i) {
                final int val = 0xFF & ebytes[i];
                esize |= val << i * 8;
            }
            if (esize > this.cs.getRemaining()) {
                throw new TTransportException("FileTransport error: bad event size");
            }
        } while (esize == 0);
        if (this.currentEvent_.getSize() < esize) {
            this.currentEvent_ = new Event(new byte[esize]);
        }
        final byte[] buf = this.currentEvent_.getBuf();
        final int nread = this.tailRead(this.inputStream_, buf, 0, esize, this.currentPolicy_);
        if (nread != esize) {
            return false;
        }
        this.currentEvent_.setAvailable(esize);
        return true;
    }
    
    @Override
    public boolean isOpen() {
        return this.inputStream_ != null && (this.readOnly_ || this.outputStream_ != null);
    }
    
    @Override
    public void open() throws TTransportException {
        if (this.isOpen()) {
            throw new TTransportException(2);
        }
        try {
            this.inputStream_ = this.createInputStream();
            this.cs = new chunkState();
            this.currentEvent_ = new Event(new byte[256]);
            if (!this.readOnly_) {
                this.outputStream_ = new BufferedOutputStream(this.inputFile_.getOutputStream(), 8192);
            }
        }
        catch (IOException iox) {
            throw new TTransportException(1, iox);
        }
    }
    
    @Override
    public void close() {
        if (this.inputFile_ != null) {
            try {
                this.inputFile_.close();
            }
            catch (IOException iox) {
                System.err.println("WARNING: Error closing input file: " + iox.getMessage());
            }
            this.inputFile_ = null;
        }
        if (this.outputStream_ != null) {
            try {
                this.outputStream_.close();
            }
            catch (IOException iox) {
                System.err.println("WARNING: Error closing output stream: " + iox.getMessage());
            }
            this.outputStream_ = null;
        }
    }
    
    public TFileTransport(final String path, final boolean readOnly) throws IOException {
        this.currentPolicy_ = tailPolicy.NOWAIT;
        this.inputFile_ = null;
        this.outputStream_ = null;
        this.currentEvent_ = null;
        this.inputStream_ = null;
        this.cs = null;
        this.readTimeout_ = 0;
        this.readOnly_ = false;
        this.inputFile_ = new TStandardFile(path);
        this.readOnly_ = readOnly;
    }
    
    public TFileTransport(final TSeekableFile inputFile, final boolean readOnly) {
        this.currentPolicy_ = tailPolicy.NOWAIT;
        this.inputFile_ = null;
        this.outputStream_ = null;
        this.currentEvent_ = null;
        this.inputStream_ = null;
        this.cs = null;
        this.readTimeout_ = 0;
        this.readOnly_ = false;
        this.inputFile_ = inputFile;
        this.readOnly_ = readOnly;
    }
    
    @Override
    public int readAll(final byte[] buf, final int off, final int len) throws TTransportException {
        int got = 0;
        for (int ret = 0; got < len; got += ret) {
            ret = this.read(buf, off + got, len - got);
            if (ret < 0) {
                throw new TTransportException("Error in reading from file");
            }
            if (ret == 0) {
                throw new TTransportException(4, "End of File reached");
            }
        }
        return got;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        if (!this.isOpen()) {
            throw new TTransportException(1, "Must open before reading");
        }
        if (this.currentEvent_.getRemaining() == 0 && !this.readEvent()) {
            return 0;
        }
        final int nread = this.currentEvent_.emit(buf, off, len);
        return nread;
    }
    
    public int getNumChunks() throws TTransportException {
        if (!this.isOpen()) {
            throw new TTransportException(1, "Must open before getNumChunks");
        }
        try {
            final long len = this.inputFile_.length();
            if (len == 0L) {
                return 0;
            }
            return (int)(len / this.cs.getChunkSize()) + 1;
        }
        catch (IOException iox) {
            throw new TTransportException(iox.getMessage(), iox);
        }
    }
    
    public int getCurChunk() throws TTransportException {
        if (!this.isOpen()) {
            throw new TTransportException(1, "Must open before getCurChunk");
        }
        return this.cs.getChunkNum();
    }
    
    public void seekToChunk(int chunk) throws TTransportException {
        if (!this.isOpen()) {
            throw new TTransportException(1, "Must open before seeking");
        }
        final int numChunks = this.getNumChunks();
        if (numChunks == 0) {
            return;
        }
        if (chunk < 0) {
            chunk += numChunks;
        }
        if (chunk < 0) {
            chunk = 0;
        }
        long eofOffset = 0L;
        final boolean seekToEnd = chunk >= numChunks;
        if (seekToEnd) {
            --chunk;
            try {
                eofOffset = this.inputFile_.length();
            }
            catch (IOException iox) {
                throw new TTransportException(iox.getMessage(), iox);
            }
        }
        if (chunk * this.cs.getChunkSize() != this.cs.getOffset()) {
            try {
                this.inputFile_.seek(chunk * (long)this.cs.getChunkSize());
            }
            catch (IOException iox) {
                System.err.println("createInputStream: " + iox.getMessage());
                throw new TTransportException("Seek to chunk " + chunk + " " + iox.getMessage(), iox);
            }
            this.cs.seek(chunk * (long)this.cs.getChunkSize());
            this.currentEvent_.setAvailable(0);
            this.inputStream_ = this.createInputStream();
        }
        if (seekToEnd) {
            final tailPolicy old = this.setTailPolicy(tailPolicy.WAIT_FOREVER);
            while (this.cs.getOffset() < eofOffset) {
                this.readEvent();
            }
            this.currentEvent_.setAvailable(0);
            this.setTailPolicy(old);
        }
    }
    
    public void seekToEnd() throws TTransportException {
        if (!this.isOpen()) {
            throw new TTransportException(1, "Must open before seeking");
        }
        this.seekToChunk(this.getNumChunks());
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        throw new TTransportException("Not Supported");
    }
    
    @Override
    public void flush() throws TTransportException {
        throw new TTransportException("Not Supported");
    }
    
    public static void main(final String[] args) throws Exception {
        int num_chunks = 10;
        if (args.length < 1 || args[0].equals("--help") || args[0].equals("-h") || args[0].equals("-?")) {
            printUsage();
        }
        if (args.length > 1) {
            try {
                num_chunks = Integer.parseInt(args[1]);
            }
            catch (Exception e) {
                System.err.println("Cannot parse " + args[1]);
                printUsage();
            }
        }
        final TFileTransport t = new TFileTransport(args[0], true);
        t.open();
        System.out.println("NumChunks=" + t.getNumChunks());
        final Random r = new Random();
        for (int j = 0; j < num_chunks; ++j) {
            final byte[] buf = new byte[4096];
            final int cnum = r.nextInt(t.getNumChunks() - 1);
            System.out.println("Reading chunk " + cnum);
            t.seekToChunk(cnum);
            for (int i = 0; i < 4096; ++i) {
                t.read(buf, 0, 4096);
            }
        }
    }
    
    private static void printUsage() {
        System.err.println("Usage: TFileTransport <filename> [num_chunks]");
        System.err.println("       (Opens and reads num_chunks chunks from file randomly)");
        System.exit(1);
    }
    
    public static class truncableBufferedInputStream extends BufferedInputStream
    {
        public void trunc() {
            final int n = 0;
            this.count = n;
            this.pos = n;
        }
        
        public truncableBufferedInputStream(final InputStream in) {
            super(in);
        }
        
        public truncableBufferedInputStream(final InputStream in, final int size) {
            super(in, size);
        }
    }
    
    public static class Event
    {
        private byte[] buf_;
        private int nread_;
        private int navailable_;
        
        public Event(final byte[] buf) {
            this.buf_ = buf;
            final int n = 0;
            this.navailable_ = n;
            this.nread_ = n;
        }
        
        public byte[] getBuf() {
            return this.buf_;
        }
        
        public int getSize() {
            return this.buf_.length;
        }
        
        public void setAvailable(final int sz) {
            this.nread_ = 0;
            this.navailable_ = sz;
        }
        
        public int getRemaining() {
            return this.navailable_ - this.nread_;
        }
        
        public int emit(final byte[] buf, final int offset, int ndesired) {
            if (ndesired == 0 || ndesired > this.getRemaining()) {
                ndesired = this.getRemaining();
            }
            if (ndesired <= 0) {
                return ndesired;
            }
            System.arraycopy(this.buf_, this.nread_, buf, offset, ndesired);
            this.nread_ += ndesired;
            return ndesired;
        }
    }
    
    public static class chunkState
    {
        public static final int DEFAULT_CHUNK_SIZE = 16777216;
        private int chunk_size_;
        private long offset_;
        
        public chunkState() {
            this.chunk_size_ = 16777216;
            this.offset_ = 0L;
        }
        
        public chunkState(final int chunk_size) {
            this.chunk_size_ = 16777216;
            this.offset_ = 0L;
            this.chunk_size_ = chunk_size;
        }
        
        public void skip(final int size) {
            this.offset_ += size;
        }
        
        public void seek(final long offset) {
            this.offset_ = offset;
        }
        
        public int getChunkSize() {
            return this.chunk_size_;
        }
        
        public int getChunkNum() {
            return (int)(this.offset_ / this.chunk_size_);
        }
        
        public int getRemaining() {
            return this.chunk_size_ - (int)(this.offset_ % this.chunk_size_);
        }
        
        public long getOffset() {
            return this.offset_;
        }
    }
    
    public enum tailPolicy
    {
        NOWAIT(0, 0), 
        WAIT_FOREVER(500, -1);
        
        public final int timeout_;
        public final int retries_;
        
        private tailPolicy(final int timeout, final int retries) {
            this.timeout_ = timeout;
            this.retries_ = retries;
        }
    }
}
