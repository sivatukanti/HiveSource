// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import parquet.org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import parquet.org.slf4j.Logger;

public class TIOStreamTransport extends TTransport
{
    private static final Logger LOGGER;
    protected InputStream inputStream_;
    protected OutputStream outputStream_;
    
    protected TIOStreamTransport() {
        this.inputStream_ = null;
        this.outputStream_ = null;
    }
    
    public TIOStreamTransport(final InputStream is) {
        this.inputStream_ = null;
        this.outputStream_ = null;
        this.inputStream_ = is;
    }
    
    public TIOStreamTransport(final OutputStream os) {
        this.inputStream_ = null;
        this.outputStream_ = null;
        this.outputStream_ = os;
    }
    
    public TIOStreamTransport(final InputStream is, final OutputStream os) {
        this.inputStream_ = null;
        this.outputStream_ = null;
        this.inputStream_ = is;
        this.outputStream_ = os;
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public void open() throws TTransportException {
    }
    
    @Override
    public void close() {
        if (this.inputStream_ != null) {
            try {
                this.inputStream_.close();
            }
            catch (IOException iox) {
                TIOStreamTransport.LOGGER.warn("Error closing input stream.", iox);
            }
            this.inputStream_ = null;
        }
        if (this.outputStream_ != null) {
            try {
                this.outputStream_.close();
            }
            catch (IOException iox) {
                TIOStreamTransport.LOGGER.warn("Error closing output stream.", iox);
            }
            this.outputStream_ = null;
        }
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        if (this.inputStream_ == null) {
            throw new TTransportException(1, "Cannot read from null inputStream");
        }
        int bytesRead;
        try {
            bytesRead = this.inputStream_.read(buf, off, len);
        }
        catch (IOException iox) {
            throw new TTransportException(0, iox);
        }
        if (bytesRead < 0) {
            throw new TTransportException(4);
        }
        return bytesRead;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        if (this.outputStream_ == null) {
            throw new TTransportException(1, "Cannot write to null outputStream");
        }
        try {
            this.outputStream_.write(buf, off, len);
        }
        catch (IOException iox) {
            throw new TTransportException(0, iox);
        }
    }
    
    @Override
    public void flush() throws TTransportException {
        if (this.outputStream_ == null) {
            throw new TTransportException(1, "Cannot flush null outputStream");
        }
        try {
            this.outputStream_.flush();
        }
        catch (IOException iox) {
            throw new TTransportException(0, iox);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TIOStreamTransport.class.getName());
    }
}
