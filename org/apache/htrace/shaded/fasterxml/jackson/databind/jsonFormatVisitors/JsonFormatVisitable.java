// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public interface JsonFormatVisitable
{
    void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper p0, final JavaType p1) throws JsonMappingException;
}
