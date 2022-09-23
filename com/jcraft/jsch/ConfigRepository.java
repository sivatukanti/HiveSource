// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface ConfigRepository
{
    public static final Config defaultConfig = new Config() {
        public String getHostname() {
            return null;
        }
        
        public String getUser() {
            return null;
        }
        
        public int getPort() {
            return -1;
        }
        
        public String getValue(final String key) {
            return null;
        }
        
        public String[] getValues(final String key) {
            return null;
        }
    };
    public static final ConfigRepository nullConfig = new ConfigRepository() {
        public Config getConfig(final String host) {
            return ConfigRepository$2.defaultConfig;
        }
    };
    
    Config getConfig(final String p0);
    
    public interface Config
    {
        String getHostname();
        
        String getUser();
        
        int getPort();
        
        String getValue(final String p0);
        
        String[] getValues(final String p0);
    }
}
