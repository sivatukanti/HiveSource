// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.fs.permission.FsPermission;

public interface Constants
{
    public static final String CONFIG_VIEWFS_PREFIX = "fs.viewfs.mounttable";
    public static final String CONFIG_VIEWFS_HOMEDIR = "homedir";
    public static final String CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE = "default";
    public static final String CONFIG_VIEWFS_PREFIX_DEFAULT_MOUNT_TABLE = "fs.viewfs.mounttable.default";
    public static final String CONFIG_VIEWFS_LINK = "link";
    public static final String CONFIG_VIEWFS_LINK_FALLBACK = "linkFallback";
    public static final String CONFIG_VIEWFS_LINK_MERGE = "linkMerge";
    public static final String CONFIG_VIEWFS_LINK_NFLY = "linkNfly";
    public static final String CONFIG_VIEWFS_LINK_MERGE_SLASH = "linkMergeSlash";
    public static final FsPermission PERMISSION_555 = new FsPermission((short)365);
    public static final String CONFIG_VIEWFS_RENAME_STRATEGY = "fs.viewfs.rename.strategy";
}
