// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.ftp;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FsServerDefaults;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.DelegateToFileSystem;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class FtpFs extends DelegateToFileSystem
{
    FtpFs(final URI theUri, final Configuration conf) throws IOException, URISyntaxException {
        super(theUri, new FTPFileSystem(), conf, "ftp", true);
    }
    
    @Override
    public int getUriDefaultPort() {
        return 21;
    }
    
    @Deprecated
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return FtpConfigKeys.getServerDefaults();
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return FtpConfigKeys.getServerDefaults();
    }
}
