// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.curator.framework.CuratorFramework;

public interface CuratorListener
{
    void eventReceived(final CuratorFramework p0, final CuratorEvent p1) throws Exception;
}
