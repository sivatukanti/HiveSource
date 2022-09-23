// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientRequest;

public interface OnStartConnectionListener
{
    ContainerListener onStart(final ClientRequest p0);
}
