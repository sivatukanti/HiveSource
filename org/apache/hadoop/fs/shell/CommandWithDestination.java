// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.Options;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.OutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import java.util.NoSuchElementException;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.Map;
import org.apache.hadoop.fs.permission.AclUtil;
import java.io.InputStream;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.fs.PathIsDirectoryException;
import org.apache.hadoop.fs.PathOperationException;
import org.apache.hadoop.fs.PathExistsException;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.PathNotFoundException;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import java.net.URI;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.EnumSet;

abstract class CommandWithDestination extends FsCommand
{
    protected PathData dst;
    private boolean overwrite;
    private boolean verifyChecksum;
    private boolean writeChecksum;
    private boolean lazyPersist;
    private boolean direct;
    private static final String RAW = "raw.";
    private static final String RESERVED_RAW = "/.reserved/raw";
    private EnumSet<FileAttribute> preserveStatus;
    
    CommandWithDestination() {
        this.overwrite = false;
        this.verifyChecksum = true;
        this.writeChecksum = true;
        this.lazyPersist = false;
        this.direct = false;
        this.preserveStatus = EnumSet.noneOf(FileAttribute.class);
    }
    
    protected void setOverwrite(final boolean flag) {
        this.overwrite = flag;
    }
    
    protected void setLazyPersist(final boolean flag) {
        this.lazyPersist = flag;
    }
    
    protected void setVerifyChecksum(final boolean flag) {
        this.verifyChecksum = flag;
    }
    
    protected void setWriteChecksum(final boolean flag) {
        this.writeChecksum = flag;
    }
    
    protected void setDirectWrite(final boolean flag) {
        this.direct = flag;
    }
    
    protected void setPreserve(final boolean preserve) {
        if (preserve) {
            this.preserve(FileAttribute.TIMESTAMPS);
            this.preserve(FileAttribute.OWNERSHIP);
            this.preserve(FileAttribute.PERMISSION);
        }
        else {
            this.preserveStatus.clear();
        }
    }
    
    private boolean shouldPreserve(final FileAttribute attribute) {
        return this.preserveStatus.contains(attribute);
    }
    
    protected void preserve(final FileAttribute fileAttribute) {
        for (final FileAttribute attribute : this.preserveStatus) {
            if (attribute.equals(fileAttribute)) {
                return;
            }
        }
        this.preserveStatus.add(fileAttribute);
    }
    
    protected void getLocalDestination(final LinkedList<String> args) throws IOException {
        final String pathString = (args.size() < 2) ? "." : args.removeLast();
        try {
            this.dst = new PathData(new URI(pathString), this.getConf());
        }
        catch (URISyntaxException e) {
            if (!Path.WINDOWS) {
                throw new IOException("unexpected URISyntaxException", e);
            }
            this.dst = new PathData(pathString, this.getConf());
        }
    }
    
    protected void getRemoteDestination(final LinkedList<String> args) throws IOException {
        if (args.size() < 2) {
            this.dst = new PathData(".", this.getConf());
        }
        else {
            final String pathString = args.removeLast();
            final PathData[] items = PathData.expandAsGlob(pathString, this.getConf());
            switch (items.length) {
                case 0: {
                    throw new PathNotFoundException(pathString);
                }
                case 1: {
                    this.dst = items[0];
                    break;
                }
                default: {
                    throw new PathIOException(pathString, "Too many matches");
                }
            }
        }
    }
    
    @Override
    protected void processArguments(final LinkedList<PathData> args) throws IOException {
        if (args.size() > 1) {
            if (!this.dst.exists) {
                throw new PathNotFoundException(this.dst.toString());
            }
            if (!this.dst.stat.isDirectory()) {
                throw new PathIsNotDirectoryException(this.dst.toString());
            }
        }
        else if (this.dst.exists) {
            if (!this.dst.stat.isDirectory() && !this.overwrite) {
                throw new PathExistsException(this.dst.toString());
            }
        }
        else if (!this.dst.parentExists()) {
            throw new PathNotFoundException(this.dst.toString()).withFullyQualifiedPath(this.dst.path.toUri().toString());
        }
        super.processArguments(args);
    }
    
    @Override
    protected void processPathArgument(final PathData src) throws IOException {
        if (src.stat.isDirectory() && src.fs.equals(this.dst.fs)) {
            final PathData target = this.getTargetPath(src);
            String srcPath = src.fs.makeQualified(src.path).toString();
            final String dstPath = this.dst.fs.makeQualified(target.path).toString();
            if (dstPath.equals(srcPath)) {
                final PathIOException e = new PathIOException(src.toString(), "are identical");
                e.setTargetPath(dstPath.toString());
                throw e;
            }
            if (!srcPath.endsWith("/")) {
                srcPath += "/";
            }
            if (dstPath.startsWith(srcPath)) {
                final PathIOException e = new PathIOException(src.toString(), "is a subdirectory of itself");
                e.setTargetPath(target.toString());
                throw e;
            }
        }
        super.processPathArgument(src);
    }
    
    @Override
    protected void processPath(final PathData src) throws IOException {
        this.processPath(src, this.getTargetPath(src));
    }
    
    protected void processPath(final PathData src, final PathData dst) throws IOException {
        if (src.stat.isSymlink()) {
            throw new PathOperationException(src.toString());
        }
        if (src.stat.isFile()) {
            this.copyFileToTarget(src, dst);
        }
        else if (src.stat.isDirectory() && !this.isRecursive()) {
            throw new PathIsDirectoryException(src.toString());
        }
    }
    
    @Override
    protected void recursePath(final PathData src) throws IOException {
        final PathData savedDst = this.dst;
        try {
            this.dst = this.getTargetPath(src);
            final boolean preserveRawXattrs = this.checkPathsForReservedRaw(src.path, this.dst.path);
            if (this.dst.exists) {
                if (!this.dst.stat.isDirectory()) {
                    throw new PathIsNotDirectoryException(this.dst.toString());
                }
            }
            else {
                if (!this.dst.fs.mkdirs(this.dst.path)) {
                    final PathIOException e = new PathIOException(this.dst.toString());
                    e.setOperation("mkdir");
                    throw e;
                }
                this.dst.refreshStatus();
            }
            super.recursePath(src);
            if (this.dst.stat.isDirectory()) {
                this.preserveAttributes(src, this.dst, preserveRawXattrs);
            }
        }
        finally {
            this.dst = savedDst;
        }
    }
    
    protected PathData getTargetPath(final PathData src) throws IOException {
        PathData target;
        if (this.getDepth() > 0 || (this.dst.exists && this.dst.stat.isDirectory())) {
            target = this.dst.getPathDataForChild(src);
        }
        else if (this.dst.representsDirectory()) {
            target = this.dst.getPathDataForChild(src);
        }
        else {
            target = this.dst;
        }
        return target;
    }
    
    protected void copyFileToTarget(final PathData src, final PathData target) throws IOException {
        final boolean preserveRawXattrs = this.checkPathsForReservedRaw(src.path, target.path);
        src.fs.setVerifyChecksum(this.verifyChecksum);
        InputStream in = null;
        try {
            in = src.fs.open(src.path);
            this.copyStreamToTarget(in, target);
            this.preserveAttributes(src, target, preserveRawXattrs);
        }
        finally {
            IOUtils.closeStream(in);
        }
    }
    
    private boolean checkPathsForReservedRaw(final Path src, final Path target) throws PathOperationException {
        final boolean srcIsRR = Path.getPathWithoutSchemeAndAuthority(src).toString().startsWith("/.reserved/raw");
        final boolean dstIsRR = Path.getPathWithoutSchemeAndAuthority(target).toString().startsWith("/.reserved/raw");
        boolean preserveRawXattrs = false;
        if (srcIsRR && !dstIsRR) {
            final String s = "' copy from '/.reserved/raw' to non '/.reserved/raw'. Either both source and target must be in '/.reserved/raw' or neither.";
            throw new PathOperationException("'" + src.toString() + "' copy from '/.reserved/raw' to non '/.reserved/raw'. Either both source and target must be in '/.reserved/raw' or neither.");
        }
        if (!srcIsRR && dstIsRR) {
            final String s = "' copy from non '/.reserved/raw' to '/.reserved/raw'. Either both source and target must be in '/.reserved/raw' or neither.";
            throw new PathOperationException("'" + this.dst.toString() + "' copy from non '/.reserved/raw' to '/.reserved/raw'. Either both source and target must be in '/.reserved/raw' or neither.");
        }
        if (srcIsRR && dstIsRR) {
            preserveRawXattrs = true;
        }
        return preserveRawXattrs;
    }
    
    protected void copyStreamToTarget(final InputStream in, final PathData target) throws IOException {
        if (target.exists && (target.stat.isDirectory() || !this.overwrite)) {
            throw new PathExistsException(target.toString());
        }
        final TargetFileSystem targetFs = new TargetFileSystem(target.fs);
        try {
            final PathData tempTarget = this.direct ? target : target.suffix("._COPYING_");
            targetFs.setWriteChecksum(this.writeChecksum);
            targetFs.writeStreamToFile(in, tempTarget, this.lazyPersist, this.direct);
            if (!this.direct) {
                targetFs.rename(tempTarget, target);
            }
        }
        finally {
            targetFs.close();
        }
    }
    
    protected void preserveAttributes(final PathData src, final PathData target, final boolean preserveRawXAttrs) throws IOException {
        if (this.shouldPreserve(FileAttribute.TIMESTAMPS)) {
            target.fs.setTimes(target.path, src.stat.getModificationTime(), src.stat.getAccessTime());
        }
        if (this.shouldPreserve(FileAttribute.OWNERSHIP)) {
            target.fs.setOwner(target.path, src.stat.getOwner(), src.stat.getGroup());
        }
        if (this.shouldPreserve(FileAttribute.PERMISSION) || this.shouldPreserve(FileAttribute.ACL)) {
            target.fs.setPermission(target.path, src.stat.getPermission());
        }
        if (this.shouldPreserve(FileAttribute.ACL) && src.stat.hasAcl()) {
            final FsPermission perm = src.stat.getPermission();
            final List<AclEntry> srcEntries = src.fs.getAclStatus(src.path).getEntries();
            final List<AclEntry> srcFullEntries = AclUtil.getAclFromPermAndEntries(perm, srcEntries);
            target.fs.setAcl(target.path, srcFullEntries);
        }
        final boolean preserveXAttrs = this.shouldPreserve(FileAttribute.XATTR);
        if (preserveXAttrs || preserveRawXAttrs) {
            final Map<String, byte[]> srcXAttrs = src.fs.getXAttrs(src.path);
            if (srcXAttrs != null) {
                for (final Map.Entry<String, byte[]> entry : srcXAttrs.entrySet()) {
                    final String xattrName = entry.getKey();
                    if (xattrName.startsWith("raw.") || preserveXAttrs) {
                        target.fs.setXAttr(target.path, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }
    
    protected enum FileAttribute
    {
        TIMESTAMPS, 
        OWNERSHIP, 
        PERMISSION, 
        ACL, 
        XATTR;
        
        public static FileAttribute getAttribute(final char symbol) {
            for (final FileAttribute attribute : values()) {
                if (attribute.name().charAt(0) == Character.toUpperCase(symbol)) {
                    return attribute;
                }
            }
            throw new NoSuchElementException("No attribute for " + symbol);
        }
    }
    
    private static class TargetFileSystem extends FilterFileSystem
    {
        TargetFileSystem(final FileSystem fs) {
            super(fs);
        }
        
        void writeStreamToFile(final InputStream in, final PathData target, final boolean lazyPersist, final boolean direct) throws IOException {
            FSDataOutputStream out = null;
            try {
                out = this.create(target, lazyPersist, direct);
                IOUtils.copyBytes(in, out, this.getConf(), true);
            }
            finally {
                IOUtils.closeStream(out);
            }
        }
        
        FSDataOutputStream create(final PathData item, final boolean lazyPersist, final boolean direct) throws IOException {
            try {
                if (lazyPersist) {
                    final EnumSet<CreateFlag> createFlags = EnumSet.of(CreateFlag.CREATE, CreateFlag.LAZY_PERSIST);
                    return this.create(item.path, FsPermission.getFileDefault().applyUMask(FsPermission.getUMask(this.getConf())), createFlags, this.getConf().getInt("io.file.buffer.size", 4096), (short)1, this.getDefaultBlockSize(), null, null);
                }
                return this.create(item.path, true);
            }
            finally {
                if (!direct) {
                    this.deleteOnExit(item.path);
                }
            }
        }
        
        void rename(final PathData src, final PathData target) throws IOException {
            if (target.exists && !this.delete(target.path, false)) {
                final PathIOException e = new PathIOException(target.toString());
                e.setOperation("delete");
                throw e;
            }
            if (!this.rename(src.path, target.path)) {
                final PathIOException e = new PathIOException(src.toString());
                e.setOperation("rename");
                e.setTargetPath(target.toString());
                throw e;
            }
            this.cancelDeleteOnExit(src.path);
        }
        
        @Override
        public void close() {
            this.processDeleteOnExit();
        }
    }
}
