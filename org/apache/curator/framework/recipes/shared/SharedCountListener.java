// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.framework.state.ConnectionStateListener;

public interface SharedCountListener extends ConnectionStateListener
{
    void countHasChanged(final SharedCountReader p0, final int p1) throws Exception;
}
