// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazydio;

import java.io.IOException;
import org.apache.hadoop.io.FloatWritable;
import java.io.InputStream;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyFloatObjectInspector;
import java.io.DataInputStream;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.lazy.LazyFloat;

public class LazyDioFloat extends LazyFloat
{
    private ByteStream.Input in;
    private DataInputStream din;
    
    public LazyDioFloat(final LazyFloatObjectInspector oi) {
        super(oi);
    }
    
    public LazyDioFloat(final LazyDioFloat copy) {
        super(copy);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        float value = 0.0f;
        try {
            this.in = new ByteStream.Input(bytes.getData(), start, length);
            this.din = new DataInputStream(this.in);
            value = this.din.readFloat();
            ((FloatWritable)this.data).set(value);
            this.isNull = false;
        }
        catch (IOException e) {
            this.isNull = true;
        }
        finally {
            try {
                this.din.close();
            }
            catch (IOException ex) {}
            try {
                this.in.close();
            }
            catch (IOException ex2) {}
        }
    }
}
