// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class SftpStatVFS
{
    private long bsize;
    private long frsize;
    private long blocks;
    private long bfree;
    private long bavail;
    private long files;
    private long ffree;
    private long favail;
    private long fsid;
    private long flag;
    private long namemax;
    int flags;
    long size;
    int uid;
    int gid;
    int permissions;
    int atime;
    int mtime;
    String[] extended;
    
    private SftpStatVFS() {
        this.flags = 0;
        this.extended = null;
    }
    
    static SftpStatVFS getStatVFS(final Buffer buf) {
        final SftpStatVFS statvfs = new SftpStatVFS();
        statvfs.bsize = buf.getLong();
        statvfs.frsize = buf.getLong();
        statvfs.blocks = buf.getLong();
        statvfs.bfree = buf.getLong();
        statvfs.bavail = buf.getLong();
        statvfs.files = buf.getLong();
        statvfs.ffree = buf.getLong();
        statvfs.favail = buf.getLong();
        statvfs.fsid = buf.getLong();
        final int flag = (int)buf.getLong();
        statvfs.namemax = buf.getLong();
        statvfs.flag = (((flag & 0x1) != 0x0) ? 1 : 0);
        final SftpStatVFS sftpStatVFS = statvfs;
        sftpStatVFS.flag |= (((flag & 0x2) != 0x0) ? 2L : 0L);
        return statvfs;
    }
    
    public long getBlockSize() {
        return this.bsize;
    }
    
    public long getFragmentSize() {
        return this.frsize;
    }
    
    public long getBlocks() {
        return this.blocks;
    }
    
    public long getFreeBlocks() {
        return this.bfree;
    }
    
    public long getAvailBlocks() {
        return this.bavail;
    }
    
    public long getINodes() {
        return this.files;
    }
    
    public long getFreeINodes() {
        return this.ffree;
    }
    
    public long getAvailINodes() {
        return this.favail;
    }
    
    public long getFileSystemID() {
        return this.fsid;
    }
    
    public long getMountFlag() {
        return this.flag;
    }
    
    public long getMaximumFilenameLength() {
        return this.namemax;
    }
    
    public long getSize() {
        return this.getFragmentSize() * this.getBlocks() / 1024L;
    }
    
    public long getUsed() {
        return this.getFragmentSize() * (this.getBlocks() - this.getFreeBlocks()) / 1024L;
    }
    
    public long getAvailForNonRoot() {
        return this.getFragmentSize() * this.getAvailBlocks() / 1024L;
    }
    
    public long getAvail() {
        return this.getFragmentSize() * this.getFreeBlocks() / 1024L;
    }
    
    public int getCapacity() {
        return (int)(100L * (this.getBlocks() - this.getFreeBlocks()) / this.getBlocks());
    }
}
