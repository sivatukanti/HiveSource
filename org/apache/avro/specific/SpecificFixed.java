// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import java.lang.reflect.Type;
import org.apache.avro.generic.GenericData;

public abstract class SpecificFixed extends GenericData.Fixed
{
    public SpecificFixed() {
        this.setSchema(SpecificData.get().getSchema(this.getClass()));
    }
    
    public SpecificFixed(final byte[] bytes) {
        this();
        this.bytes(bytes);
    }
}
