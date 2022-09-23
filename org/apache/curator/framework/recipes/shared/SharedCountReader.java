// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.framework.listen.Listenable;

public interface SharedCountReader extends Listenable<SharedCountListener>
{
    int getCount();
    
    VersionedValue<Integer> getVersionedValue();
}
