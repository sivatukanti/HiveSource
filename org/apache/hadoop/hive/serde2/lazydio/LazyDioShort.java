// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazydio;

import java.io.IOException;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import java.io.InputStream;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyShortObjectInspector;
import java.io.DataInputStream;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.lazy.LazyShort;

public class LazyDioShort extends LazyShort
{
    private ByteStream.Input in;
    private DataInputStream din;
    
    public LazyDioShort(final LazyShortObjectInspector oi) {
        super(oi);
    }
    
    public LazyDioShort(final LazyDioShort copy) {
        super(copy);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        short value = 0;
        try {
            this.in = new ByteStream.Input(bytes.getData(), start, length);
            this.din = new DataInputStream(this.in);
            value = this.din.readShort();
            ((ShortWritable)this.data).set(value);
            this.isNull = false;
        }
        catch (Exception e) {
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
