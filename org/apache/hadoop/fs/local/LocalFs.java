// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.local;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.ChecksumFs;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class LocalFs extends ChecksumFs
{
    LocalFs(final Configuration conf) throws IOException, URISyntaxException {
        super(new RawLocalFs(conf));
    }
    
    LocalFs(final URI theUri, final Configuration conf) throws IOException, URISyntaxException {
        this(conf);
    }
}
