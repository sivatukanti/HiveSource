// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class MultipartUploader
{
    public static final Logger LOG;
    
    public abstract UploadHandle initialize(final Path p0) throws IOException;
    
    public abstract PartHandle putPart(final Path p0, final InputStream p1, final int p2, final UploadHandle p3, final long p4) throws IOException;
    
    public abstract PathHandle complete(final Path p0, final List<Pair<Integer, PartHandle>> p1, final UploadHandle p2) throws IOException;
    
    public abstract void abort(final Path p0, final UploadHandle p1) throws IOException;
    
    protected void checkUploadId(final byte[] uploadId) throws IllegalArgumentException {
        Preconditions.checkArgument(uploadId.length > 0, (Object)"Empty UploadId is not valid");
    }
    
    static {
        LOG = LoggerFactory.getLogger(MultipartUploader.class);
    }
}
