// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyBooleanObjectInspector;

public class LazyBoolean extends LazyPrimitive<LazyBooleanObjectInspector, BooleanWritable>
{
    public LazyBoolean(final LazyBooleanObjectInspector oi) {
        super(oi);
        this.data = (T)new BooleanWritable();
    }
    
    public LazyBoolean(final LazyBoolean copy) {
        super(copy);
        this.data = (T)new BooleanWritable(((BooleanWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        if (length == 4 && Character.toUpperCase(bytes.getData()[start]) == 84 && Character.toUpperCase(bytes.getData()[start + 1]) == 82 && Character.toUpperCase(bytes.getData()[start + 2]) == 85 && Character.toUpperCase(bytes.getData()[start + 3]) == 69) {
            ((BooleanWritable)this.data).set(true);
            this.isNull = false;
        }
        else if (length == 5 && Character.toUpperCase(bytes.getData()[start]) == 70 && Character.toUpperCase(bytes.getData()[start + 1]) == 65 && Character.toUpperCase(bytes.getData()[start + 2]) == 76 && Character.toUpperCase(bytes.getData()[start + 3]) == 83 && Character.toUpperCase(bytes.getData()[start + 4]) == 69) {
            ((BooleanWritable)this.data).set(false);
            this.isNull = false;
        }
        else if (((LazyBooleanObjectInspector)this.oi).isExtendedLiteral()) {
            if (length == 1) {
                final byte c = bytes.getData()[start];
                if (c == 49 || c == 116 || c == 84) {
                    ((BooleanWritable)this.data).set(true);
                    this.isNull = false;
                }
                else if (c == 48 || c == 102 || c == 70) {
                    ((BooleanWritable)this.data).set(false);
                    this.isNull = false;
                }
                else {
                    this.isNull = true;
                }
            }
        }
        else {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "BOOLEAN");
        }
    }
}
