// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;

public interface JsonFormatVisitorWithSerializerProvider
{
    SerializerProvider getProvider();
    
    void setProvider(final SerializerProvider p0);
}
