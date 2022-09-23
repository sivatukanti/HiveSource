// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl;

import java.io.InputStream;
import java.util.Properties;

public final class BuildId
{
    private static String buildId;
    
    private static String _initiateBuildId() {
        String id = "Jersey";
        final InputStream in = getIntputStream();
        if (in != null) {
            try {
                final Properties p = new Properties();
                p.load(in);
                final String _id = p.getProperty("Build-Id");
                if (_id != null) {
                    id = id + ": " + _id;
                }
            }
            catch (Exception e) {}
            finally {
                close(in);
            }
        }
        return id;
    }
    
    private static void close(final InputStream in) {
        try {
            in.close();
        }
        catch (Exception ex) {}
    }
    
    private static InputStream getIntputStream() {
        try {
            return BuildId.class.getResourceAsStream("build.properties");
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static final String getBuildId() {
        return BuildId.buildId;
    }
    
    static {
        BuildId.buildId = _initiateBuildId();
    }
}
