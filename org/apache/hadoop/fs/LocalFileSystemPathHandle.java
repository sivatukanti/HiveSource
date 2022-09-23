// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Objects;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class LocalFileSystemPathHandle implements PathHandle
{
    private final String path;
    private final Long mtime;
    
    public LocalFileSystemPathHandle(final String path, final Optional<Long> mtime) {
        this.path = path;
        this.mtime = mtime.orElse(null);
    }
    
    public LocalFileSystemPathHandle(final ByteBuffer bytes) throws IOException {
        if (null == bytes) {
            throw new IOException("Missing PathHandle");
        }
        final FSProtos.LocalFileSystemPathHandleProto p = FSProtos.LocalFileSystemPathHandleProto.parseFrom(ByteString.copyFrom(bytes));
        this.path = (p.hasPath() ? p.getPath() : null);
        this.mtime = (p.hasMtime() ? Long.valueOf(p.getMtime()) : null);
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void verify(final FileStatus stat) throws InvalidPathHandleException {
        if (null == stat) {
            throw new InvalidPathHandleException("Could not resolve handle");
        }
        if (this.mtime != null && this.mtime != stat.getModificationTime()) {
            throw new InvalidPathHandleException("Content changed");
        }
    }
    
    @Override
    public ByteBuffer bytes() {
        final FSProtos.LocalFileSystemPathHandleProto.Builder b = FSProtos.LocalFileSystemPathHandleProto.newBuilder();
        b.setPath(this.path);
        if (this.mtime != null) {
            b.setMtime(this.mtime);
        }
        return b.build().toByteString().asReadOnlyByteBuffer();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final LocalFileSystemPathHandle that = (LocalFileSystemPathHandle)o;
        return Objects.equals(this.path, that.path) && Objects.equals(this.mtime, that.mtime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.mtime);
    }
    
    @Override
    public String toString() {
        return "LocalFileSystemPathHandle{path='" + this.path + '\'' + ", mtime=" + this.mtime + '}';
    }
}
