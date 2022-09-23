// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.deployer;

import java.io.FileNotFoundException;
import java.util.Map;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import org.mortbay.resource.Resource;

public class FileConfigurationManager implements ConfigurationManager
{
    private Resource _file;
    private Properties _properties;
    
    public FileConfigurationManager() {
        this._properties = new Properties();
    }
    
    public void setFile(final String filename) throws MalformedURLException, IOException {
        this._file = Resource.newResource(filename);
    }
    
    public Map getProperties() {
        try {
            this.loadProperties();
            return this._properties;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void loadProperties() throws FileNotFoundException, IOException {
        if (this._properties.isEmpty()) {
            this._properties.load(this._file.getInputStream());
        }
    }
}
