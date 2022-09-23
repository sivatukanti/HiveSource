// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

public class Configured implements Configurable
{
    private Config config;
    
    public Configured() {
        this.config = null;
    }
    
    public Configured(final Config config) {
        this.config = config;
    }
    
    @Override
    public Config getConfig() {
        return this.config;
    }
    
    @Override
    public void setConfig(final Config config) {
        this.config = config;
    }
}
