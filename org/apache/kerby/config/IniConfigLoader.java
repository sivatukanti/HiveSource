// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

public class IniConfigLoader extends ConfigLoader
{
    private static final String COMMENT_SYMBOL = "#";
    private ConfigImpl rootConfig;
    private ConfigImpl currentConfig;
    
    @Override
    protected void loadConfig(final ConfigImpl config, final Resource resource) throws IOException {
        this.rootConfig = config;
        this.currentConfig = config;
        final InputStream is = (InputStream)resource.getResource();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            this.parseLine(line);
        }
        reader.close();
    }
    
    private void parseLine(String line) {
        if (line == null) {
            return;
        }
        line = line.trim();
        if (line.startsWith("#")) {
            return;
        }
        if (line.matches("\\[.*\\]")) {
            final String subConfigName = line.replaceFirst("\\[(.*)\\]", "$1");
            final ConfigImpl subConfig = new ConfigImpl(subConfigName);
            this.rootConfig.set(subConfigName, subConfig);
            this.currentConfig = subConfig;
        }
        else if (line.matches(".*=.*")) {
            final int i = line.indexOf(61);
            final String name = line.substring(0, i).trim();
            final String value = line.substring(i + 1).trim();
            this.currentConfig.set(name, value);
        }
    }
}
