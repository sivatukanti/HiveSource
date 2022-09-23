// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.Iterator;

public interface ResourceCollection extends Iterable<Resource>
{
    Iterator<Resource> iterator();
    
    int size();
    
    boolean isFilesystemOnly();
}
