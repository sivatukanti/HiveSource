// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.depend;

import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import org.apache.tools.ant.types.Path;

public interface DependencyAnalyzer
{
    void addSourcePath(final Path p0);
    
    void addClassPath(final Path p0);
    
    void addRootClass(final String p0);
    
    Enumeration<File> getFileDependencies();
    
    Enumeration<String> getClassDependencies();
    
    void reset();
    
    void config(final String p0, final Object p1);
    
    void setClosure(final boolean p0);
    
    File getClassContainer(final String p0) throws IOException;
    
    File getSourceContainer(final String p0) throws IOException;
}
