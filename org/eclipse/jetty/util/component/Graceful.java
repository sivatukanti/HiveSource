// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import java.util.concurrent.Future;

public interface Graceful
{
    Future<Void> shutdown();
}
