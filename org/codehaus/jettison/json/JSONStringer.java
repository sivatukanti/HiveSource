// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.json;

import java.io.Writer;
import java.io.StringWriter;

public class JSONStringer extends JSONWriter
{
    public JSONStringer() {
        super(new StringWriter());
    }
    
    public String toString() {
        return (super.mode == 'd') ? super.writer.toString() : null;
    }
}
