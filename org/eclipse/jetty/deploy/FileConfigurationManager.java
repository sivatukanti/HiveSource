// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Properties;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.util.resource.Resource;

public class FileConfigurationManager implements ConfigurationManager
{
    private Resource _file;
    private Map<String, String> _map;
    
    public FileConfigurationManager() {
        this._map = new HashMap<String, String>();
    }
    
    public void setFile(final String filename) throws MalformedURLException, IOException {
        this._file = Resource.newResource(filename);
    }
    
    public Map<String, String> getProperties() {
        try {
            this.loadProperties();
            return this._map;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void loadProperties() throws FileNotFoundException, IOException {
        if (this._map.isEmpty() && this._file != null) {
            final Properties properties = new Properties();
            properties.load(this._file.getInputStream());
            for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
                this._map.put(entry.getKey().toString(), String.valueOf(entry.getValue()));
            }
        }
    }
}
