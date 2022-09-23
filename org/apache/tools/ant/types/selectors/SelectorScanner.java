// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

public interface SelectorScanner
{
    void setSelectors(final FileSelector[] p0);
    
    String[] getDeselectedDirectories();
    
    String[] getDeselectedFiles();
}
