// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.FilenameFilter;
import java.io.File;

public interface FileInterface
{
    String getName();
    
    String getPath();
    
    String getAbsolutePath();
    
    String getParent();
    
    boolean exists();
    
    boolean canWrite();
    
    boolean canRead();
    
    boolean isFile();
    
    boolean isDirectory();
    
    boolean isAbsolute();
    
    long lastModified();
    
    long length();
    
    boolean mkdir();
    
    boolean renameTo(final File p0);
    
    boolean mkdirs();
    
    String[] list();
    
    String[] list(final FilenameFilter p0);
    
    boolean delete();
}
