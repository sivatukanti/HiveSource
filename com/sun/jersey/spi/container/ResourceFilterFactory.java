// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.util.List;
import com.sun.jersey.api.model.AbstractMethod;

public interface ResourceFilterFactory
{
    List<ResourceFilter> create(final AbstractMethod p0);
}
