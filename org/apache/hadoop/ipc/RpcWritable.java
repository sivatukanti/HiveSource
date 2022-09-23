// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import com.google.protobuf.CodedInputStream;
import java.io.OutputStream;
import com.google.protobuf.CodedOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import com.google.protobuf.Message;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Private
public abstract class RpcWritable implements Writable
{
    static RpcWritable wrap(final Object o) {
        if (o instanceof RpcWritable) {
            return (RpcWritable)o;
        }
        if (o instanceof Message) {
            return new ProtobufWrapper((Message)o);
        }
        if (o instanceof Writable) {
            return new WritableWrapper((Writable)o);
        }
        throw new IllegalArgumentException("Cannot wrap " + o.getClass());
    }
    
    @Override
    public final void readFields(final DataInput in) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final void write(final DataOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    abstract void writeTo(final ResponseBuffer p0) throws IOException;
    
    abstract <T> T readFrom(final ByteBuffer p0) throws IOException;
    
    static class WritableWrapper extends RpcWritable
    {
        private final Writable writable;
        
        WritableWrapper(final Writable writable) {
            this.writable = writable;
        }
        
        public void writeTo(final ResponseBuffer out) throws IOException {
            this.writable.write(out);
        }
        
        @Override
         <T> T readFrom(final ByteBuffer bb) throws IOException {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(bb.array(), bb.position() + bb.arrayOffset(), bb.remaining()));
            try {
                this.writable.readFields(in);
            }
            finally {
                bb.position(bb.limit() - in.available());
            }
            return (T)this.writable;
        }
    }
    
    static class ProtobufWrapper extends RpcWritable
    {
        private Message message;
        
        ProtobufWrapper(final Message message) {
            this.message = message;
        }
        
        Message getMessage() {
            return this.message;
        }
        
        @Override
        void writeTo(final ResponseBuffer out) throws IOException {
            int length = this.message.getSerializedSize();
            length += CodedOutputStream.computeRawVarint32Size(length);
            out.ensureCapacity(length);
            this.message.writeDelimitedTo(out);
        }
        
        @Override
         <T> T readFrom(final ByteBuffer bb) throws IOException {
            final CodedInputStream cis = CodedInputStream.newInstance(bb.array(), bb.position() + bb.arrayOffset(), bb.remaining());
            try {
                cis.pushLimit(cis.readRawVarint32());
                this.message = (Message)this.message.getParserForType().parseFrom(cis);
                cis.checkLastTagWas(0);
            }
            finally {
                bb.position(bb.position() + cis.getTotalBytesRead());
            }
            return (T)this.message;
        }
    }
    
    public static class Buffer extends RpcWritable
    {
        private ByteBuffer bb;
        
        public static Buffer wrap(final ByteBuffer bb) {
            return new Buffer(bb);
        }
        
        Buffer() {
        }
        
        Buffer(final ByteBuffer bb) {
            this.bb = bb;
        }
        
        ByteBuffer getByteBuffer() {
            return this.bb;
        }
        
        @Override
        void writeTo(final ResponseBuffer out) throws IOException {
            out.ensureCapacity(this.bb.remaining());
            out.write(this.bb.array(), this.bb.position() + this.bb.arrayOffset(), this.bb.remaining());
        }
        
        @Override
         <T> T readFrom(final ByteBuffer bb) throws IOException {
            this.bb = bb.slice();
            bb.limit(bb.position());
            return (T)this;
        }
        
        public <T> T newInstance(final Class<T> valueClass, final Configuration conf) throws IOException {
            T instance;
            try {
                instance = valueClass.newInstance();
                if (instance instanceof Configurable) {
                    ((Configurable)instance).setConf(conf);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this.getValue(instance);
        }
        
        public <T> T getValue(final T value) throws IOException {
            return RpcWritable.wrap(value).readFrom(this.bb);
        }
        
        public int remaining() {
            return this.bb.remaining();
        }
    }
}
