// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.protocolPB;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FSProtos;

public final class PBHelper
{
    private PBHelper() {
    }
    
    public static FsPermission convert(final FSProtos.FsPermissionProto proto) throws IOException {
        return new FsPermission((short)proto.getPerm());
    }
    
    public static FSProtos.FsPermissionProto convert(final FsPermission p) throws IOException {
        final FSProtos.FsPermissionProto.Builder bld = FSProtos.FsPermissionProto.newBuilder();
        bld.setPerm(p.toShort());
        return bld.build();
    }
    
    public static FileStatus convert(final FSProtos.FileStatusProto proto) throws IOException {
        boolean isdir = false;
        Path symlink = null;
        long blocksize = 0L;
        long length = 0L;
        short blockReplication = 0;
        switch (proto.getFileType()) {
            case FT_DIR: {
                isdir = true;
                symlink = null;
                blocksize = 0L;
                length = 0L;
                blockReplication = 0;
                break;
            }
            case FT_SYMLINK: {
                isdir = false;
                symlink = new Path(proto.getSymlink());
                blocksize = 0L;
                length = 0L;
                blockReplication = 0;
                break;
            }
            case FT_FILE: {
                isdir = false;
                symlink = null;
                blocksize = proto.getBlockSize();
                length = proto.getLength();
                final int brep = proto.getBlockReplication();
                if ((brep & 0xFFFF0000) != 0x0) {
                    throw new IOException(String.format("Block replication 0x%08x doesn't fit in 16 bits.", brep));
                }
                blockReplication = (short)brep;
                break;
            }
            default: {
                throw new IllegalStateException("Unknown type: " + proto.getFileType());
            }
        }
        final Path path = new Path(proto.getPath());
        final long mtime = proto.getModificationTime();
        final long atime = proto.getAccessTime();
        final FsPermission permission = convert(proto.getPermission());
        final String owner = proto.getOwner();
        final String group = proto.getGroup();
        final int flags = proto.getFlags();
        final FileStatus fileStatus = new FileStatus(length, isdir, blockReplication, blocksize, mtime, atime, permission, owner, group, symlink, path, FileStatus.attributes((flags & 0x1) != 0x0, (flags & 0x2) != 0x0, (flags & 0x4) != 0x0, (flags & 0x8) != 0x0));
        return fileStatus;
    }
    
    public static FSProtos.FileStatusProto convert(final FileStatus stat) throws IOException {
        final FSProtos.FileStatusProto.Builder bld = FSProtos.FileStatusProto.newBuilder();
        bld.setPath(stat.getPath().toString());
        if (stat.isDirectory()) {
            bld.setFileType(FSProtos.FileStatusProto.FileType.FT_DIR);
        }
        else if (stat.isSymlink()) {
            bld.setFileType(FSProtos.FileStatusProto.FileType.FT_SYMLINK).setSymlink(stat.getSymlink().toString());
        }
        else {
            bld.setFileType(FSProtos.FileStatusProto.FileType.FT_FILE).setLength(stat.getLen()).setBlockReplication(stat.getReplication()).setBlockSize(stat.getBlockSize());
        }
        bld.setAccessTime(stat.getAccessTime()).setModificationTime(stat.getModificationTime()).setOwner(stat.getOwner()).setGroup(stat.getGroup()).setPermission(convert(stat.getPermission()));
        int flags = 0;
        flags |= (stat.hasAcl() ? 1 : 0);
        flags |= (stat.isEncrypted() ? 2 : 0);
        flags |= (stat.isErasureCoded() ? 4 : 0);
        flags |= (stat.isSnapshotEnabled() ? 8 : 0);
        bld.setFlags(flags);
        return bld.build();
    }
}
