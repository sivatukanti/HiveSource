// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.io.InputBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.RawComparator;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public abstract class DeserializerComparator<T> implements RawComparator<T>
{
    private InputBuffer buffer;
    private Deserializer<T> deserializer;
    private T key1;
    private T key2;
    
    protected DeserializerComparator(final Deserializer<T> deserializer) throws IOException {
        this.buffer = new InputBuffer();
        (this.deserializer = deserializer).open(this.buffer);
    }
    
    @Override
    public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
        try {
            this.buffer.reset(b1, s1, l1);
            this.key1 = this.deserializer.deserialize(this.key1);
            this.buffer.reset(b2, s2, l2);
            this.key2 = this.deserializer.deserialize(this.key2);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.compare(this.key1, this.key2);
    }
}
