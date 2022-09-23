// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.local;

import java.io.IOException;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.CommonConfigurationKeys;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LocalConfigKeys extends CommonConfigurationKeys
{
    public static final String BLOCK_SIZE_KEY = "file.blocksize";
    public static final long BLOCK_SIZE_DEFAULT = 67108864L;
    public static final String REPLICATION_KEY = "file.replication";
    public static final short REPLICATION_DEFAULT = 1;
    public static final String STREAM_BUFFER_SIZE_KEY = "file.stream-buffer-size";
    public static final int STREAM_BUFFER_SIZE_DEFAULT = 4096;
    public static final String BYTES_PER_CHECKSUM_KEY = "file.bytes-per-checksum";
    public static final int BYTES_PER_CHECKSUM_DEFAULT = 512;
    public static final String CLIENT_WRITE_PACKET_SIZE_KEY = "file.client-write-packet-size";
    public static final int CLIENT_WRITE_PACKET_SIZE_DEFAULT = 65536;
    public static final boolean ENCRYPT_DATA_TRANSFER_DEFAULT = false;
    public static final long FS_TRASH_INTERVAL_DEFAULT = 0L;
    public static final DataChecksum.Type CHECKSUM_TYPE_DEFAULT;
    public static final String KEY_PROVIDER_URI_DEFAULT = "";
    
    public static FsServerDefaults getServerDefaults() throws IOException {
        return new FsServerDefaults(67108864L, 512, 65536, (short)1, 4096, false, 0L, LocalConfigKeys.CHECKSUM_TYPE_DEFAULT, "");
    }
    
    static {
        CHECKSUM_TYPE_DEFAULT = DataChecksum.Type.CRC32;
    }
}
