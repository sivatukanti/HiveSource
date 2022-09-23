// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Set;
import org.apache.hadoop.fs.permission.FsPermission;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class LocatedFileStatus extends FileStatus
{
    private static final long serialVersionUID = 389257504L;
    private BlockLocation[] locations;
    
    public LocatedFileStatus() {
    }
    
    public LocatedFileStatus(final FileStatus stat, final BlockLocation[] locations) {
        this(stat.getLen(), stat.isDirectory(), stat.getReplication(), stat.getBlockSize(), stat.getModificationTime(), stat.getAccessTime(), stat.getPermission(), stat.getOwner(), stat.getGroup(), null, stat.getPath(), stat.hasAcl(), stat.isEncrypted(), stat.isErasureCoded(), locations);
        if (stat.isSymlink()) {
            try {
                this.setSymlink(stat.getSymlink());
            }
            catch (IOException e) {
                throw new RuntimeException("Unexpected exception", e);
            }
        }
    }
    
    @Deprecated
    public LocatedFileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path symlink, final Path path, final BlockLocation[] locations) {
        this(length, isdir, block_replication, blocksize, modification_time, access_time, permission, owner, group, symlink, path, permission != null && permission.getAclBit(), permission != null && permission.getEncryptedBit(), permission != null && permission.getErasureCodedBit(), locations);
    }
    
    public LocatedFileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path symlink, final Path path, final boolean hasAcl, final boolean isEncrypted, final boolean isErasureCoded, final BlockLocation[] locations) {
        this(length, isdir, block_replication, blocksize, modification_time, access_time, permission, owner, group, symlink, path, FileStatus.attributes(hasAcl, isEncrypted, isErasureCoded, false), locations);
        this.locations = locations;
    }
    
    public LocatedFileStatus(final long length, final boolean isdir, final int block_replication, final long blocksize, final long modification_time, final long access_time, final FsPermission permission, final String owner, final String group, final Path symlink, final Path path, final Set<AttrFlags> attr, final BlockLocation[] locations) {
        super(length, isdir, block_replication, blocksize, modification_time, access_time, permission, owner, group, symlink, path, attr);
        this.locations = locations;
    }
    
    public BlockLocation[] getBlockLocations() {
        return this.locations;
    }
    
    protected void setBlockLocations(final BlockLocation[] locations) {
        this.locations = locations;
    }
    
    @Override
    public int compareTo(final FileStatus o) {
        return super.compareTo(o);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
