// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

public abstract class ConfigLoader
{
    private Resource resource;
    private ConfigImpl config;
    
    protected void setResource(final Resource resource) {
        this.resource = resource;
    }
    
    protected void setConfig(final ConfigImpl config) {
        this.config = config;
    }
    
    public Config load() {
        if (this.config == null) {
            this.config = new ConfigImpl(this.resource.getName());
        }
        this.config.reset();
        try {
            this.loadConfig(this.config, this.resource);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load " + ConfigLoader.class.getPackage().getName(), e);
        }
        return this.config;
    }
    
    protected abstract void loadConfig(final ConfigImpl p0, final Resource p1) throws Exception;
}
