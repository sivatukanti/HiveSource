// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.net.URI;

public class ProxyFileSystem extends FilterFileSystem
{
    protected String myScheme;
    protected String myAuthority;
    protected URI myUri;
    protected String realScheme;
    protected String realAuthority;
    protected URI realUri;
    
    protected Path swizzleParamPath(final Path p) {
        final String pathUriString = p.toUri().toString();
        final URI newPathUri = URI.create(pathUriString);
        return new Path(this.realScheme, this.realAuthority, newPathUri.getPath());
    }
    
    private Path swizzleReturnPath(final Path p) {
        final String pathUriString = p.toUri().toString();
        final URI newPathUri = URI.create(pathUriString);
        return new Path(this.myScheme, this.myAuthority, newPathUri.getPath());
    }
    
    protected FileStatus swizzleFileStatus(final FileStatus orig, final boolean isParam) {
        final FileStatus ret = new FileStatus(orig.getLen(), orig.isDir(), orig.getReplication(), orig.getBlockSize(), orig.getModificationTime(), orig.getAccessTime(), orig.getPermission(), orig.getOwner(), orig.getGroup(), isParam ? this.swizzleParamPath(orig.getPath()) : this.swizzleReturnPath(orig.getPath()));
        return ret;
    }
    
    public ProxyFileSystem() {
        throw new RuntimeException("Unsupported constructor");
    }
    
    public ProxyFileSystem(final FileSystem fs) {
        throw new RuntimeException("Unsupported constructor");
    }
    
    @Override
    public Path resolvePath(final Path p) throws IOException {
        this.checkPath(p);
        return this.getFileStatus(p).getPath();
    }
    
    public ProxyFileSystem(final FileSystem fs, final URI myUri) {
        super(fs);
        final URI realUri = fs.getUri();
        this.realScheme = realUri.getScheme();
        this.realAuthority = realUri.getAuthority();
        this.realUri = realUri;
        this.myScheme = myUri.getScheme();
        this.myAuthority = myUri.getAuthority();
        this.myUri = myUri;
    }
    
    @Override
    public void initialize(final URI name, final Configuration conf) throws IOException {
        try {
            final URI realUri = new URI(this.realScheme, this.realAuthority, name.getPath(), name.getQuery(), name.getFragment());
            super.initialize(realUri, conf);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public URI getUri() {
        return this.myUri;
    }
    
    @Override
    public String getName() {
        return this.getUri().toString();
    }
    
    @Override
    public Path makeQualified(final Path path) {
        return this.swizzleReturnPath(super.makeQualified(this.swizzleParamPath(path)));
    }
    
    @Override
    protected void checkPath(final Path path) {
        super.checkPath(this.swizzleParamPath(path));
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final FileStatus file, final long start, final long len) throws IOException {
        return super.getFileBlockLocations(this.swizzleFileStatus(file, true), start, len);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        return super.open(this.swizzleParamPath(f), bufferSize);
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        return super.append(this.swizzleParamPath(f), bufferSize, progress);
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return super.create(this.swizzleParamPath(f), permission, overwrite, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public boolean setReplication(final Path src, final short replication) throws IOException {
        return super.setReplication(this.swizzleParamPath(src), replication);
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        return super.rename(this.swizzleParamPath(src), this.swizzleParamPath(dst));
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        return super.delete(this.swizzleParamPath(f), recursive);
    }
    
    @Override
    public boolean deleteOnExit(final Path f) throws IOException {
        return super.deleteOnExit(this.swizzleParamPath(f));
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        final FileStatus[] orig = super.listStatus(this.swizzleParamPath(f));
        final FileStatus[] ret = new FileStatus[orig.length];
        for (int i = 0; i < orig.length; ++i) {
            ret[i] = this.swizzleFileStatus(orig[i], false);
        }
        return ret;
    }
    
    @Override
    public Path getHomeDirectory() {
        return this.swizzleReturnPath(super.getHomeDirectory());
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
        super.setWorkingDirectory(this.swizzleParamPath(newDir));
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.swizzleReturnPath(super.getWorkingDirectory());
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        return super.mkdirs(this.swizzleParamPath(f), permission);
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        super.copyFromLocalFile(delSrc, this.swizzleParamPath(src), this.swizzleParamPath(dst));
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path[] srcs, final Path dst) throws IOException {
        super.copyFromLocalFile(delSrc, overwrite, srcs, this.swizzleParamPath(dst));
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path src, final Path dst) throws IOException {
        super.copyFromLocalFile(delSrc, overwrite, src, this.swizzleParamPath(dst));
    }
    
    @Override
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        super.copyToLocalFile(delSrc, this.swizzleParamPath(src), dst);
    }
    
    @Override
    public Path startLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        return super.startLocalOutput(this.swizzleParamPath(fsOutputFile), tmpLocalFile);
    }
    
    @Override
    public void completeLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        super.completeLocalOutput(this.swizzleParamPath(fsOutputFile), tmpLocalFile);
    }
    
    @Override
    public ContentSummary getContentSummary(final Path f) throws IOException {
        return super.getContentSummary(this.swizzleParamPath(f));
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        return this.swizzleFileStatus(super.getFileStatus(this.swizzleParamPath(f)), false);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws IOException {
        return super.getFileChecksum(this.swizzleParamPath(f));
    }
    
    @Override
    public void setOwner(final Path p, final String username, final String groupname) throws IOException {
        super.setOwner(this.swizzleParamPath(p), username, groupname);
    }
    
    @Override
    public void setTimes(final Path p, final long mtime, final long atime) throws IOException {
        super.setTimes(this.swizzleParamPath(p), mtime, atime);
    }
    
    @Override
    public void setPermission(final Path p, final FsPermission permission) throws IOException {
        super.setPermission(this.swizzleParamPath(p), permission);
    }
}
