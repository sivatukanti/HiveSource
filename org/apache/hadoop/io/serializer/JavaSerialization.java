// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class JavaSerialization implements Serialization<Serializable>
{
    @InterfaceAudience.Private
    @Override
    public boolean accept(final Class<?> c) {
        return Serializable.class.isAssignableFrom(c);
    }
    
    @InterfaceAudience.Private
    @Override
    public Deserializer<Serializable> getDeserializer(final Class<Serializable> c) {
        return new JavaSerializationDeserializer<Serializable>();
    }
    
    @InterfaceAudience.Private
    @Override
    public Serializer<Serializable> getSerializer(final Class<Serializable> c) {
        return new JavaSerializationSerializer();
    }
    
    static class JavaSerializationDeserializer<T extends Serializable> implements Deserializer<T>
    {
        private ObjectInputStream ois;
        
        @Override
        public void open(final InputStream in) throws IOException {
            this.ois = new ObjectInputStream(in) {
                @Override
                protected void readStreamHeader() {
                }
            };
        }
        
        @Override
        public T deserialize(final T object) throws IOException {
            try {
                return (T)this.ois.readObject();
            }
            catch (ClassNotFoundException e) {
                throw new IOException(e.toString());
            }
        }
        
        @Override
        public void close() throws IOException {
            this.ois.close();
        }
    }
    
    static class JavaSerializationSerializer implements Serializer<Serializable>
    {
        private ObjectOutputStream oos;
        
        @Override
        public void open(final OutputStream out) throws IOException {
            this.oos = new ObjectOutputStream(out) {
                @Override
                protected void writeStreamHeader() {
                }
            };
        }
        
        @Override
        public void serialize(final Serializable object) throws IOException {
            this.oos.reset();
            this.oos.writeObject(object);
        }
        
        @Override
        public void close() throws IOException {
            this.oos.close();
        }
    }
}
