// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.async;

import java.util.concurrent.Future;

public interface FutureListener<T>
{
    void onComplete(final Future<T> p0) throws InterruptedException;
}
