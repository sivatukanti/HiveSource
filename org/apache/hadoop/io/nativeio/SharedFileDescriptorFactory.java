// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.nativeio;

import org.slf4j.LoggerFactory;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SharedFileDescriptorFactory
{
    public static final Logger LOG;
    private final String prefix;
    private final String path;
    
    public static String getLoadingFailureReason() {
        if (!NativeIO.isAvailable()) {
            return "NativeIO is not available.";
        }
        if (!SystemUtils.IS_OS_UNIX) {
            return "The OS is not UNIX.";
        }
        return null;
    }
    
    public static SharedFileDescriptorFactory create(final String prefix, final String[] paths) throws IOException {
        final String loadingFailureReason = getLoadingFailureReason();
        if (loadingFailureReason != null) {
            throw new IOException(loadingFailureReason);
        }
        if (paths.length == 0) {
            throw new IOException("no SharedFileDescriptorFactory paths were configured.");
        }
        final StringBuilder errors = new StringBuilder();
        String strPrefix = "";
        final int length = paths.length;
        int i = 0;
        while (i < length) {
            final String path = paths[i];
            try {
                final FileInputStream fis = new FileInputStream(createDescriptor0(prefix + "test", path, 1));
                fis.close();
                deleteStaleTemporaryFiles0(prefix, path);
                return new SharedFileDescriptorFactory(prefix, path);
            }
            catch (IOException e) {
                errors.append(strPrefix).append("Error creating file descriptor in ").append(path).append(": ").append(e.getMessage());
                strPrefix = ", ";
                ++i;
                continue;
            }
            break;
        }
        throw new IOException(errors.toString());
    }
    
    private SharedFileDescriptorFactory(final String prefix, final String path) {
        this.prefix = prefix;
        this.path = path;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public FileInputStream createDescriptor(final String info, final int length) throws IOException {
        return new FileInputStream(createDescriptor0(this.prefix + info, this.path, length));
    }
    
    private static native void deleteStaleTemporaryFiles0(final String p0, final String p1) throws IOException;
    
    private static native FileDescriptor createDescriptor0(final String p0, final String p1, final int p2) throws IOException;
    
    static {
        LOG = LoggerFactory.getLogger(SharedFileDescriptorFactory.class);
    }
}
