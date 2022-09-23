// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.io.File;

public final class SysUtil
{
    private SysUtil() {
    }
    
    public static File getTempDir() {
        final String tmpDir = System.getProperty("java.io.tmpdir");
        return new File(tmpDir);
    }
}
