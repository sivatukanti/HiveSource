// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.local;

import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.RawLocalFileSystem;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.fs.FsConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.DelegateToFileSystem;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class RawLocalFs extends DelegateToFileSystem
{
    RawLocalFs(final Configuration conf) throws IOException, URISyntaxException {
        this(FsConstants.LOCAL_FS_URI, conf);
    }
    
    RawLocalFs(final URI theUri, final Configuration conf) throws IOException, URISyntaxException {
        super(theUri, new RawLocalFileSystem(), conf, FsConstants.LOCAL_FS_URI.getScheme(), false);
    }
    
    @Override
    public int getUriDefaultPort() {
        return -1;
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return LocalConfigKeys.getServerDefaults();
    }
    
    @Deprecated
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return LocalConfigKeys.getServerDefaults();
    }
    
    @Override
    public boolean isValidName(final String src) {
        return true;
    }
}
