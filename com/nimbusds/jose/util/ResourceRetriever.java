// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.io.IOException;
import java.net.URL;

public interface ResourceRetriever
{
    Resource retrieveResource(final URL p0) throws IOException;
}
