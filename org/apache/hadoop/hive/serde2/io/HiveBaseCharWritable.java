// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.hadoop.io.Text;

public abstract class HiveBaseCharWritable
{
    protected Text value;
    
    public HiveBaseCharWritable() {
        this.value = new Text();
    }
    
    public int getCharacterLength() {
        return HiveStringUtils.getTextUtfLength(this.value);
    }
    
    public Text getTextValue() {
        return this.value;
    }
    
    public void readFields(final DataInput in) throws IOException {
        this.value.readFields(in);
    }
    
    public void write(final DataOutput out) throws IOException {
        this.value.write(out);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass() == this.getClass() && this.value.equals(((HiveBaseCharWritable)obj).value);
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
