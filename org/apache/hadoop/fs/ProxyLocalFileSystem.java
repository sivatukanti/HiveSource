// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;

public class ProxyLocalFileSystem extends FilterFileSystem
{
    protected LocalFileSystem localFs;
    
    public ProxyLocalFileSystem() {
        this.localFs = new LocalFileSystem();
    }
    
    public ProxyLocalFileSystem(final FileSystem fs) {
        throw new RuntimeException("Unsupported Constructor");
    }
    
    @Override
    public void initialize(URI name, final Configuration conf) throws IOException {
        final String scheme = name.getScheme();
        String nameUriString = name.toString();
        if (Shell.WINDOWS) {
            nameUriString = nameUriString.replaceAll("%5C", "/").replaceFirst("/[c-zC-Z]:", "/").replaceFirst("^[c-zC-Z]:", "");
            name = URI.create(nameUriString);
        }
        final String authority = (name.getAuthority() != null) ? name.getAuthority() : "";
        final String proxyUriString = nameUriString + "://" + authority + "/";
        (this.fs = ShimLoader.getHadoopShims().createProxyFileSystem(this.localFs, URI.create(proxyUriString))).initialize(name, conf);
    }
}
