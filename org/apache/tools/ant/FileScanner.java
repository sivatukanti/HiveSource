// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.io.File;

public interface FileScanner
{
    void addDefaultExcludes();
    
    File getBasedir();
    
    String[] getExcludedDirectories();
    
    String[] getExcludedFiles();
    
    String[] getIncludedDirectories();
    
    String[] getIncludedFiles();
    
    String[] getNotIncludedDirectories();
    
    String[] getNotIncludedFiles();
    
    void scan() throws IllegalStateException;
    
    void setBasedir(final String p0);
    
    void setBasedir(final File p0);
    
    void setExcludes(final String[] p0);
    
    void setIncludes(final String[] p0);
    
    void setCaseSensitive(final boolean p0);
}
