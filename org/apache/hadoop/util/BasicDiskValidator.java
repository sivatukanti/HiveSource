// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.File;

public class BasicDiskValidator implements DiskValidator
{
    public static final String NAME = "basic";
    
    @Override
    public void checkStatus(final File dir) throws DiskChecker.DiskErrorException {
        DiskChecker.checkDir(dir);
    }
}
