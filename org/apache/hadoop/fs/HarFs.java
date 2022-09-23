// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;

public class HarFs extends DelegateToFileSystem
{
    HarFs(final URI theUri, final Configuration conf) throws IOException, URISyntaxException {
        super(theUri, new HarFileSystem(), conf, "har", false);
    }
    
    @Override
    public int getUriDefaultPort() {
        return -1;
    }
}
