// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Map;

public class StandardConstantMapObjectInspector extends StandardMapObjectInspector implements ConstantObjectInspector
{
    private Map<?, ?> value;
    
    protected StandardConstantMapObjectInspector() {
    }
    
    protected StandardConstantMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector, final Map<?, ?> value) {
        super(mapKeyObjectInspector, mapValueObjectInspector);
        this.value = value;
    }
    
    @Override
    public Map<?, ?> getWritableConstantValue() {
        return this.value;
    }
}
