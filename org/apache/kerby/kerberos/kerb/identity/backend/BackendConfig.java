// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity.backend;

import java.io.File;
import org.apache.kerby.config.Conf;

public class BackendConfig extends Conf
{
    private File confDir;
    
    public void setConfDir(final File dir) {
        this.confDir = dir;
    }
    
    public File getConfDir() {
        return this.confDir;
    }
}
