// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.net.URISyntaxException;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.net.URL;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.URLConnection;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class FsUrlConnection extends URLConnection
{
    private static final Logger LOG;
    private Configuration conf;
    private InputStream is;
    
    FsUrlConnection(final Configuration conf, final URL url) {
        super(url);
        Preconditions.checkArgument(conf != null, (Object)"null conf argument");
        Preconditions.checkArgument(url != null, (Object)"null url argument");
        this.conf = conf;
    }
    
    @Override
    public void connect() throws IOException {
        Preconditions.checkState(this.is == null, (Object)"Already connected");
        try {
            FsUrlConnection.LOG.debug("Connecting to {}", this.url);
            final FileSystem fs = FileSystem.get(this.url.toURI(), this.conf);
            this.is = fs.open(new Path(this.url.toURI()));
        }
        catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.is == null) {
            this.connect();
        }
        return this.is;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FsUrlConnection.class);
    }
}
