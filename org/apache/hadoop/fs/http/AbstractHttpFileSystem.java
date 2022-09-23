// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.http;

import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.fs.Seekable;
import java.io.FilterInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.net.URLConnection;
import java.io.InputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;

abstract class AbstractHttpFileSystem extends FileSystem
{
    private static final long DEFAULT_BLOCK_SIZE = 4096L;
    private static final Path WORKING_DIR;
    private URI uri;
    
    @Override
    public void initialize(final URI name, final Configuration conf) throws IOException {
        super.initialize(name, conf);
        this.uri = name;
    }
    
    @Override
    public abstract String getScheme();
    
    @Override
    public URI getUri() {
        return this.uri;
    }
    
    @Override
    public FSDataInputStream open(final Path path, final int bufferSize) throws IOException {
        final URLConnection conn = path.toUri().toURL().openConnection();
        final InputStream in = conn.getInputStream();
        return new FSDataInputStream(new HttpDataInputStream(in));
    }
    
    @Override
    public FSDataOutputStream create(final Path path, final FsPermission fsPermission, final boolean b, final int i, final short i1, final long l, final Progressable progressable) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FSDataOutputStream append(final Path path, final int i, final Progressable progressable) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean rename(final Path path, final Path path1) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean delete(final Path path, final boolean b) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileStatus[] listStatus(final Path path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setWorkingDirectory(final Path path) {
    }
    
    @Override
    public Path getWorkingDirectory() {
        return AbstractHttpFileSystem.WORKING_DIR;
    }
    
    @Override
    public boolean mkdirs(final Path path, final FsPermission fsPermission) throws IOException {
        return false;
    }
    
    @Override
    public FileStatus getFileStatus(final Path path) throws IOException {
        return new FileStatus(-1L, false, 1, 4096L, 0L, path);
    }
    
    static {
        WORKING_DIR = new Path("/");
    }
    
    private static class HttpDataInputStream extends FilterInputStream implements Seekable, PositionedReadable
    {
        HttpDataInputStream(final InputStream in) {
            super(in);
        }
        
        @Override
        public int read(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void readFully(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void readFully(final long position, final byte[] buffer) throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void seek(final long pos) throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getPos() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean seekToNewSource(final long targetPos) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
