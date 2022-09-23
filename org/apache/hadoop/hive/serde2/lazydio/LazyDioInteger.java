// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazydio;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import java.io.InputStream;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyIntObjectInspector;
import java.io.DataInputStream;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;

public class LazyDioInteger extends LazyInteger
{
    private ByteStream.Input in;
    private DataInputStream din;
    
    public LazyDioInteger(final LazyIntObjectInspector oi) {
        super(oi);
    }
    
    public LazyDioInteger(final LazyDioInteger copy) {
        super(copy);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        int value = 0;
        try {
            this.in = new ByteStream.Input(bytes.getData(), start, length);
            this.din = new DataInputStream(this.in);
            value = this.din.readInt();
            ((IntWritable)this.data).set(value);
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
