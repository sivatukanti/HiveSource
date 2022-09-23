// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class MultipartUploaderFactory
{
    public static final Logger LOG;
    private static ServiceLoader<MultipartUploaderFactory> serviceLoader;
    
    public static MultipartUploader get(final FileSystem fs, final Configuration conf) throws IOException {
        MultipartUploader mpu = null;
        for (final MultipartUploaderFactory factory : MultipartUploaderFactory.serviceLoader) {
            mpu = factory.createMultipartUploader(fs, conf);
            if (mpu != null) {
                break;
            }
        }
        return mpu;
    }
    
    protected abstract MultipartUploader createMultipartUploader(final FileSystem p0, final Configuration p1) throws IOException;
    
    static {
        LOG = LoggerFactory.getLogger(MultipartUploaderFactory.class);
        MultipartUploaderFactory.serviceLoader = ServiceLoader.load(MultipartUploaderFactory.class, MultipartUploaderFactory.class.getClassLoader());
        final Iterator<MultipartUploaderFactory> iterServices = MultipartUploaderFactory.serviceLoader.iterator();
        while (iterServices.hasNext()) {
            iterServices.next();
        }
    }
}
