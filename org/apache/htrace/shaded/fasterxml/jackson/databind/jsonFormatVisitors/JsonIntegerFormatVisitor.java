// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;

public interface JsonIntegerFormatVisitor extends JsonValueFormatVisitor
{
    void numberType(final JsonParser.NumberType p0);
    
    public static class Base extends JsonValueFormatVisitor.Base implements JsonIntegerFormatVisitor
    {
        @Override
        public void numberType(final JsonParser.NumberType type) {
        }
    }
}
