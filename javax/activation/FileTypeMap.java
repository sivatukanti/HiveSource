// 
// Decompiled by Procyon v0.5.36
// 

package javax.activation;

import java.io.File;

public abstract class FileTypeMap
{
    private static FileTypeMap defaultMap;
    
    public abstract String getContentType(final File p0);
    
    public abstract String getContentType(final String p0);
    
    public static void setDefaultFileTypeMap(final FileTypeMap map) {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            try {
                security.checkSetFactory();
            }
            catch (SecurityException ex) {
                if (FileTypeMap.class.getClassLoader() != map.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        FileTypeMap.defaultMap = map;
    }
    
    public static FileTypeMap getDefaultFileTypeMap() {
        if (FileTypeMap.defaultMap == null) {
            FileTypeMap.defaultMap = new MimetypesFileTypeMap();
        }
        return FileTypeMap.defaultMap;
    }
    
    static {
        FileTypeMap.defaultMap = null;
    }
}
