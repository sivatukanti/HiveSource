// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.net.URLConnection;
import java.io.IOException;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.URLStreamHandler;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class FsUrlStreamHandler extends URLStreamHandler
{
    private Configuration conf;
    
    FsUrlStreamHandler(final Configuration conf) {
        this.conf = conf;
    }
    
    FsUrlStreamHandler() {
        this.conf = new Configuration();
    }
    
    @Override
    protected FsUrlConnection openConnection(final URL url) throws IOException {
        return new FsUrlConnection(this.conf, url);
    }
}
