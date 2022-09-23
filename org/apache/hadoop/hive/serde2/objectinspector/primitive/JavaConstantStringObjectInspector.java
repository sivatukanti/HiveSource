// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantStringObjectInspector extends JavaStringObjectInspector implements ConstantObjectInspector
{
    private String value;
    
    public JavaConstantStringObjectInspector(final String value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new Text(this.value);
    }
}
