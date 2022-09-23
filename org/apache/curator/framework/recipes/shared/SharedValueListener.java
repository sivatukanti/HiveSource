// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.framework.state.ConnectionStateListener;

public interface SharedValueListener extends ConnectionStateListener
{
    void valueHasChanged(final SharedValueReader p0, final byte[] p1) throws Exception;
}
