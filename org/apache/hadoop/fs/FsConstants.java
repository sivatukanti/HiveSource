// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface FsConstants
{
    public static final URI LOCAL_FS_URI = URI.create("file:///");
    public static final String FTP_SCHEME = "ftp";
    public static final int MAX_PATH_LINKS = 32;
    public static final URI VIEWFS_URI = URI.create("viewfs:///");
    public static final String VIEWFS_SCHEME = "viewfs";
}
