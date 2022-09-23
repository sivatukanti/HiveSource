// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.List;

public class StandardConstantListObjectInspector extends StandardListObjectInspector implements ConstantObjectInspector
{
    private List<?> value;
    
    protected StandardConstantListObjectInspector() {
    }
    
    protected StandardConstantListObjectInspector(final ObjectInspector listElementObjectInspector, final List<?> value) {
        super(listElementObjectInspector);
        this.value = value;
    }
    
    @Override
    public List<?> getWritableConstantValue() {
        return this.value;
    }
}
