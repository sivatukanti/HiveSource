// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Collections;
import java.io.InvalidObjectException;
import java.io.DataOutput;
import org.apache.hadoop.fs.protocolPB.PBHelper;
import java.io.DataInput;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FileStatus implements Writable, Comparable<Object>, Serializable, ObjectInputValidation
{
    private static final long serialVersionUID = 332065512L;
    private Path path;
    private long length;
    private Boolean isdir;
    private short block_replication;
    private long blocksize;
    private long modification_time;
    private long access_time;
    private FsPermission permission;
    private String owner;
    private String group;
    private Path symlink;
    private Set<AttrFlags> attr;
    public static final Set<AttrFlags> NONE;
    
    public static Set<AttrFlags> attributes(final boolean acl, final boolean crypt, final boolean ec, final boolean sn) {
        if (!acl && !crypt && !ec && !sn) {
            return FileStatus.NONE;
        }
        final EnumSet<AttrFlags> ret = EnumSet.noneOf(AttrFlags.class);
        if (acl) {
            ret.add(AttrFlags.HAS_ACL);
        }
        if (crypt) {
            ret.add(AttrFlags.HAS_CRYPT);
        }
        if (ec) {
            ret.add(AttrFlags.HAS_EC);
        }
        if (sn) {
            ret.add(AttrFlags.SNAPSHOT_ENABLED);
        }
        return ret;
    }
    
    public FileStatus() {
        this(0L, false, 0, 0L, 0L, 0L, null, null, null, null);
    }
    
    public FileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final Path path) {
        this(length, isdir, block_replication, blocksize, modification_time, 0L, null, null, null, path);
    }
    
    public FileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path path) {
        this(length, isdir, block_replication, blocksize, modification_time, access_time, permission, owner, group, null, path);
    }
    
    public FileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path symlink, final Path path) {
        this(length, isdir, block_replication, blocksize, modification_time, access_time, permission, owner, group, symlink, path, false, false, false);
    }
    
    public FileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path symlink, final Path path, final boolean hasAcl, final boolean isEncrypted, final boolean isErasureCoded) {
        this(length, isdir, block_replication, blocksize, modification_time, access_time, permission, owner, group, symlink, path, attributes(hasAcl, isEncrypted, isErasureCoded, false));
    }
    
    public FileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path symlink, final Path path, final Set<AttrFlags> attr) {
        this.length = length;
        this.isdir = isdir;
        this.block_replication = (short)block_replication;
        this.blocksize = blocksize;
        this.modification_time = modification_time;
        this.access_time = access_time;
        if (permission != null) {
            this.permission = permission;
        }
        else if (isdir) {
            this.permission = FsPermission.getDirDefault();
        }
        else if (symlink != null) {
            this.permission = FsPermission.getDefault();
        }
        else {
            this.permission = FsPermission.getFileDefault();
        }
        this.owner = ((owner == null) ? "" : owner);
        this.group = ((group == null) ? "" : group);
        this.symlink = symlink;
        this.path = path;
        this.attr = attr;
        assert !isdir;
    }
    
    public FileStatus(final FileStatus other) throws IOException {
        this(other.getLen(), other.isDirectory(), other.getReplication(), other.getBlockSize(), other.getModificationTime(), other.getAccessTime(), other.getPermission(), other.getOwner(), other.getGroup(), other.isSymlink() ? other.getSymlink() : null, other.getPath());
    }
    
    public long getLen() {
        return this.length;
    }
    
    public boolean isFile() {
        return !this.isDirectory() && !this.isSymlink();
    }
    
    public boolean isDirectory() {
        return this.isdir;
    }
    
    @Deprecated
    public final boolean isDir() {
        return this.isDirectory();
    }
    
    public boolean isSymlink() {
        return this.symlink != null;
    }
    
    public long getBlockSize() {
        return this.blocksize;
    }
    
    public short getReplication() {
        return this.block_replication;
    }
    
    public long getModificationTime() {
        return this.modification_time;
    }
    
    public long getAccessTime() {
        return this.access_time;
    }
    
    public FsPermission getPermission() {
        return this.permission;
    }
    
    public boolean hasAcl() {
        return this.attr.contains(AttrFlags.HAS_ACL);
    }
    
    public boolean isEncrypted() {
        return this.attr.contains(AttrFlags.HAS_CRYPT);
    }
    
    public boolean isErasureCoded() {
        return this.attr.contains(AttrFlags.HAS_EC);
    }
    
    public boolean isSnapshotEnabled() {
        return this.attr.contains(AttrFlags.SNAPSHOT_ENABLED);
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public Path getPath() {
        return this.path;
    }
    
    public void setPath(final Path p) {
        this.path = p;
    }
    
    protected void setPermission(final FsPermission permission) {
        this.permission = ((permission == null) ? FsPermission.getFileDefault() : permission);
    }
    
    protected void setOwner(final String owner) {
        this.owner = ((owner == null) ? "" : owner);
    }
    
    protected void setGroup(final String group) {
        this.group = ((group == null) ? "" : group);
    }
    
    public Path getSymlink() throws IOException {
        if (!this.isSymlink()) {
            throw new IOException("Path " + this.path + " is not a symbolic link");
        }
        return this.symlink;
    }
    
    public void setSymlink(final Path p) {
        this.symlink = p;
    }
    
    public int compareTo(final FileStatus o) {
        return this.getPath().compareTo(o.getPath());
    }
    
    @Override
    public int compareTo(final Object o) {
        final FileStatus other = (FileStatus)o;
        return this.compareTo(other);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FileStatus)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        final FileStatus other = (FileStatus)o;
        return this.getPath().equals(other.getPath());
    }
    
    @Override
    public int hashCode() {
        return this.getPath().hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("path=" + this.path);
        sb.append("; isDirectory=" + this.isdir);
        if (!this.isDirectory()) {
            sb.append("; length=" + this.length);
            sb.append("; replication=" + this.block_replication);
            sb.append("; blocksize=" + this.blocksize);
        }
        sb.append("; modification_time=" + this.modification_time);
        sb.append("; access_time=" + this.access_time);
        sb.append("; owner=" + this.owner);
        sb.append("; group=" + this.group);
        sb.append("; permission=" + this.permission);
        sb.append("; isSymlink=" + this.isSymlink());
        if (this.isSymlink()) {
            try {
                sb.append("; symlink=" + this.getSymlink());
            }
            catch (IOException e) {
                throw new RuntimeException("Unexpected exception", e);
            }
        }
        sb.append("; hasAcl=" + this.hasAcl());
        sb.append("; isEncrypted=" + this.isEncrypted());
        sb.append("; isErasureCoded=" + this.isErasureCoded());
        sb.append("}");
        return sb.toString();
    }
    
    @Deprecated
    @Override
    public void readFields(final DataInput in) throws IOException {
        final int size = in.readInt();
        if (size < 0) {
            throw new IOException("Can't read FileStatusProto with negative size of " + size);
        }
        final byte[] buf = new byte[size];
        in.readFully(buf);
        final FSProtos.FileStatusProto proto = FSProtos.FileStatusProto.parseFrom(buf);
        final FileStatus other = PBHelper.convert(proto);
        this.isdir = other.isDirectory();
        this.length = other.getLen();
        this.block_replication = other.getReplication();
        this.blocksize = other.getBlockSize();
        this.modification_time = other.getModificationTime();
        this.access_time = other.getAccessTime();
        this.setPermission(other.getPermission());
        this.setOwner(other.getOwner());
        this.setGroup(other.getGroup());
        this.setSymlink(other.isSymlink() ? other.getSymlink() : null);
        this.setPath(other.getPath());
        this.attr = attributes(other.hasAcl(), other.isEncrypted(), other.isErasureCoded(), other.isSnapshotEnabled());
        assert !this.isSymlink() : "A directory cannot be a symlink";
    }
    
    @Deprecated
    @Override
    public void write(final DataOutput out) throws IOException {
        final FSProtos.FileStatusProto proto = PBHelper.convert(this);
        final int size = proto.getSerializedSize();
        out.writeInt(size);
        out.write(proto.toByteArray());
    }
    
    @Override
    public void validateObject() throws InvalidObjectException {
        if (null == this.path) {
            throw new InvalidObjectException("No Path in deserialized FileStatus");
        }
        if (null == this.isdir) {
            throw new InvalidObjectException("No type in deserialized FileStatus");
        }
    }
    
    static {
        NONE = Collections.emptySet();
    }
    
    public enum AttrFlags
    {
        HAS_ACL, 
        HAS_CRYPT, 
        HAS_EC, 
        SNAPSHOT_ENABLED;
    }
}
