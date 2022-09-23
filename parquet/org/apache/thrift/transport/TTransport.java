// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

public abstract class TTransport
{
    public abstract boolean isOpen();
    
    public boolean peek() {
        return this.isOpen();
    }
    
    public abstract void open() throws TTransportException;
    
    public abstract void close();
    
    public abstract int read(final byte[] p0, final int p1, final int p2) throws TTransportException;
    
    public int readAll(final byte[] buf, final int off, final int len) throws TTransportException {
        int got = 0;
        for (int ret = 0; got < len; got += ret) {
            ret = this.read(buf, off + got, len - got);
            if (ret <= 0) {
                throw new TTransportException("Cannot read. Remote side has closed. Tried to read " + len + " bytes, but only got " + got + " bytes. (This is often indicative of an internal error on the server side. Please check your server logs.)");
            }
        }
        return got;
    }
    
    public void write(final byte[] buf) throws TTransportException {
        this.write(buf, 0, buf.length);
    }
    
    public abstract void write(final byte[] p0, final int p1, final int p2) throws TTransportException;
    
    public void flush() throws TTransportException {
    }
    
    public byte[] getBuffer() {
        return null;
    }
    
    public int getBufferPosition() {
        return 0;
    }
    
    public int getBytesRemainingInBuffer() {
        return -1;
    }
    
    public void consumeBuffer(final int len) {
    }
}
