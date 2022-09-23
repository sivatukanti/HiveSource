// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.BlockLocation;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.LocatedFileStatus;

class ViewFsLocatedFileStatus extends LocatedFileStatus
{
    final LocatedFileStatus myFs;
    Path modifiedPath;
    
    ViewFsLocatedFileStatus(final LocatedFileStatus locatedFileStatus, final Path path) {
        this.myFs = locatedFileStatus;
        this.modifiedPath = path;
    }
    
    @Override
    public long getLen() {
        return this.myFs.getLen();
    }
    
    @Override
    public boolean isFile() {
        return this.myFs.isFile();
    }
    
    @Override
    public boolean isDirectory() {
        return this.myFs.isDirectory();
    }
    
    @Override
    public boolean isSymlink() {
        return this.myFs.isSymlink();
    }
    
    @Override
    public long getBlockSize() {
        return this.myFs.getBlockSize();
    }
    
    @Override
    public short getReplication() {
        return this.myFs.getReplication();
    }
    
    @Override
    public long getModificationTime() {
        return this.myFs.getModificationTime();
    }
    
    @Override
    public long getAccessTime() {
        return this.myFs.getAccessTime();
    }
    
    @Override
    public FsPermission getPermission() {
        return this.myFs.getPermission();
    }
    
    @Override
    public String getOwner() {
        return this.myFs.getOwner();
    }
    
    @Override
    public String getGroup() {
        return this.myFs.getGroup();
    }
    
    @Override
    public Path getPath() {
        return this.modifiedPath;
    }
    
    @Override
    public void setPath(final Path p) {
        this.modifiedPath = p;
    }
    
    @Override
    public Path getSymlink() throws IOException {
        return this.myFs.getSymlink();
    }
    
    @Override
    public void setSymlink(final Path p) {
        this.myFs.setSymlink(p);
    }
    
    @Override
    public BlockLocation[] getBlockLocations() {
        return this.myFs.getBlockLocations();
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
