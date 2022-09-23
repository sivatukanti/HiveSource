// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.util;

import java.io.File;

public class FileID
{
    public static boolean isWebArchive(final File path) {
        if (path.isFile()) {
            final String name = path.getName().toLowerCase();
            return name.endsWith(".war") || name.endsWith(".jar");
        }
        final File webInf = new File(path, "WEB-INF");
        final File webXml = new File(webInf, "web.xml");
        return webXml.exists() && webXml.isFile();
    }
    
    public static boolean isWebArchiveFile(final File path) {
        if (!path.isFile()) {
            return false;
        }
        final String name = path.getName().toLowerCase();
        return name.endsWith(".war") || name.endsWith(".jar");
    }
    
    public static boolean isXmlFile(final File path) {
        if (!path.isFile()) {
            return false;
        }
        final String name = path.getName().toLowerCase();
        return name.endsWith(".xml");
    }
}
