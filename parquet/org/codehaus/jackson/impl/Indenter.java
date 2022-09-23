// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.impl;

import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.JsonGenerator;

public interface Indenter
{
    void writeIndentation(final JsonGenerator p0, final int p1) throws IOException, JsonGenerationException;
    
    boolean isInline();
}
