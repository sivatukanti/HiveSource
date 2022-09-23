// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import java.util.EventListener;

public interface ContinuationListener extends EventListener
{
    void onComplete(final Continuation p0);
    
    void onTimeout(final Continuation p0);
}
