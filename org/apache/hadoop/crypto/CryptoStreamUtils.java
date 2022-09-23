// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.Seekable;
import java.io.InputStream;
import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.util.CleanerUtil;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class CryptoStreamUtils
{
    private static final int MIN_BUFFER_SIZE = 512;
    private static final Logger LOG;
    
    public static void freeDB(final ByteBuffer buffer) {
        if (CleanerUtil.UNMAP_SUPPORTED) {
            try {
                CleanerUtil.getCleaner().freeBuffer(buffer);
            }
            catch (IOException e) {
                CryptoStreamUtils.LOG.info("Failed to free the buffer", e);
            }
        }
        else {
            CryptoStreamUtils.LOG.trace(CleanerUtil.UNMAP_NOT_SUPPORTED_REASON);
        }
    }
    
    public static int getBufferSize(final Configuration conf) {
        return conf.getInt("hadoop.security.crypto.buffer.size", 8192);
    }
    
    public static void checkCodec(final CryptoCodec codec) {
        if (codec.getCipherSuite() != CipherSuite.AES_CTR_NOPADDING) {
            throw new UnsupportedCodecException("AES/CTR/NoPadding is required");
        }
    }
    
    public static int checkBufferSize(final CryptoCodec codec, final int bufferSize) {
        Preconditions.checkArgument(bufferSize >= 512, (Object)"Minimum value of buffer size is 512.");
        return bufferSize - bufferSize % codec.getCipherSuite().getAlgorithmBlockSize();
    }
    
    public static long getInputStreamOffset(final InputStream in) throws IOException {
        if (in instanceof Seekable) {
            return ((Seekable)in).getPos();
        }
        return 0L;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CryptoStreamUtils.class);
    }
}
