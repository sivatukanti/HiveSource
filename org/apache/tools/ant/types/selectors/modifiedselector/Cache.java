// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors.modifiedselector;

import java.util.Iterator;

public interface Cache
{
    boolean isValid();
    
    void delete();
    
    void load();
    
    void save();
    
    Object get(final Object p0);
    
    void put(final Object p0, final Object p1);
    
    Iterator<String> iterator();
}
