// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TStandardFile implements TSeekableFile
{
    protected String path_;
    protected RandomAccessFile inputFile_;
    
    public TStandardFile(final String path) throws IOException {
        this.path_ = null;
        this.inputFile_ = null;
        this.path_ = path;
        this.inputFile_ = new RandomAccessFile(this.path_, "r");
    }
    
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.inputFile_.getFD());
    }
    
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this.path_);
    }
    
    public void close() throws IOException {
        if (this.inputFile_ != null) {
            this.inputFile_.close();
        }
    }
    
    public long length() throws IOException {
        return this.inputFile_.length();
    }
    
    public void seek(final long pos) throws IOException {
        this.inputFile_.seek(pos);
    }
}
