// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.util.ReflectionUtils;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import java.io.DataInputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class WritableSerialization extends Configured implements Serialization<Writable>
{
    @InterfaceAudience.Private
    @Override
    public boolean accept(final Class<?> c) {
        return Writable.class.isAssignableFrom(c);
    }
    
    @InterfaceAudience.Private
    @Override
    public Serializer<Writable> getSerializer(final Class<Writable> c) {
        return new WritableSerializer();
    }
    
    @InterfaceAudience.Private
    @Override
    public Deserializer<Writable> getDeserializer(final Class<Writable> c) {
        return new WritableDeserializer(this.getConf(), c);
    }
    
    static class WritableDeserializer extends Configured implements Deserializer<Writable>
    {
        private Class<?> writableClass;
        private DataInputStream dataIn;
        
        public WritableDeserializer(final Configuration conf, final Class<?> c) {
            this.setConf(conf);
            this.writableClass = c;
        }
        
        @Override
        public void open(final InputStream in) {
            if (in instanceof DataInputStream) {
                this.dataIn = (DataInputStream)in;
            }
            else {
                this.dataIn = new DataInputStream(in);
            }
        }
        
        @Override
        public Writable deserialize(final Writable w) throws IOException {
            Writable writable;
            if (w == null) {
                writable = ReflectionUtils.newInstance(this.writableClass, this.getConf());
            }
            else {
                writable = w;
            }
            writable.readFields(this.dataIn);
            return writable;
        }
        
        @Override
        public void close() throws IOException {
            this.dataIn.close();
        }
    }
    
    static class WritableSerializer extends Configured implements Serializer<Writable>
    {
        private DataOutputStream dataOut;
        
        @Override
        public void open(final OutputStream out) {
            if (out instanceof DataOutputStream) {
                this.dataOut = (DataOutputStream)out;
            }
            else {
                this.dataOut = new DataOutputStream(out);
            }
        }
        
        @Override
        public void serialize(final Writable w) throws IOException {
            w.write(this.dataOut);
        }
        
        @Override
        public void close() throws IOException {
            this.dataOut.close();
        }
    }
}
