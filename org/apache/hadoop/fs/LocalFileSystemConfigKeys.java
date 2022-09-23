// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LocalFileSystemConfigKeys extends CommonConfigurationKeys
{
    public static final String LOCAL_FS_BLOCK_SIZE_KEY = "file.blocksize";
    public static final long LOCAL_FS_BLOCK_SIZE_DEFAULT = 67108864L;
    public static final String LOCAL_FS_REPLICATION_KEY = "file.replication";
    public static final short LOCAL_FS_REPLICATION_DEFAULT = 1;
    public static final String LOCAL_FS_STREAM_BUFFER_SIZE_KEY = "file.stream-buffer-size";
    public static final int LOCAL_FS_STREAM_BUFFER_SIZE_DEFAULT = 4096;
    public static final String LOCAL_FS_BYTES_PER_CHECKSUM_KEY = "file.bytes-per-checksum";
    public static final int LOCAL_FS_BYTES_PER_CHECKSUM_DEFAULT = 512;
    public static final String LOCAL_FS_CLIENT_WRITE_PACKET_SIZE_KEY = "file.client-write-packet-size";
    public static final int LOCAL_FS_CLIENT_WRITE_PACKET_SIZE_DEFAULT = 65536;
}
