// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory
{
    InputStream create(final InputStream p0) throws IOException;
}
