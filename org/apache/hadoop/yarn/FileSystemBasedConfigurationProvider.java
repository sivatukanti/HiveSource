// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.conf.ConfigurationProvider;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FileSystemBasedConfigurationProvider extends ConfigurationProvider
{
    private static final Log LOG;
    private FileSystem fs;
    private Path configDir;
    
    @Override
    public synchronized InputStream getConfigurationInputStream(final Configuration bootstrapConf, final String name) throws IOException, YarnException {
        if (name == null || name.isEmpty()) {
            throw new YarnException("Illegal argument! The parameter should not be null or empty");
        }
        Path filePath;
        if (YarnConfiguration.RM_CONFIGURATION_FILES.contains(name)) {
            filePath = new Path(this.configDir, name);
            if (!this.fs.exists(filePath)) {
                FileSystemBasedConfigurationProvider.LOG.info(filePath + " not found");
                return null;
            }
        }
        else {
            filePath = new Path(name);
            if (!this.fs.exists(filePath)) {
                FileSystemBasedConfigurationProvider.LOG.info(filePath + " not found");
                return null;
            }
        }
        return this.fs.open(filePath);
    }
    
    @Override
    public synchronized void initInternal(final Configuration bootstrapConf) throws Exception {
        this.configDir = new Path(bootstrapConf.get("yarn.resourcemanager.configuration.file-system-based-store", "/yarn/conf"));
        this.fs = this.configDir.getFileSystem(bootstrapConf);
        if (!this.fs.exists(this.configDir)) {
            this.fs.mkdirs(this.configDir);
        }
    }
    
    @Override
    public synchronized void closeInternal() throws Exception {
        this.fs.close();
    }
    
    static {
        LOG = LogFactory.getLog(FileSystemBasedConfigurationProvider.class);
    }
}
