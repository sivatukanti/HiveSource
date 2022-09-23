// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer.avro;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Decoder;
import java.io.InputStream;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.Schema;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.serializer.Serialization;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AvroSerialization<T> extends Configured implements Serialization<T>
{
    @InterfaceAudience.Private
    public static final String AVRO_SCHEMA_KEY = "Avro-Schema";
    
    @InterfaceAudience.Private
    @Override
    public Deserializer<T> getDeserializer(final Class<T> c) {
        return new AvroDeserializer(c);
    }
    
    @InterfaceAudience.Private
    @Override
    public Serializer<T> getSerializer(final Class<T> c) {
        return new AvroSerializer(c);
    }
    
    @InterfaceAudience.Private
    public abstract Schema getSchema(final T p0);
    
    @InterfaceAudience.Private
    public abstract DatumWriter<T> getWriter(final Class<T> p0);
    
    @InterfaceAudience.Private
    public abstract DatumReader<T> getReader(final Class<T> p0);
    
    class AvroSerializer implements Serializer<T>
    {
        private DatumWriter<T> writer;
        private BinaryEncoder encoder;
        private OutputStream outStream;
        
        AvroSerializer(final Class<T> clazz) {
            this.writer = AvroSerialization.this.getWriter(clazz);
        }
        
        @Override
        public void close() throws IOException {
            this.encoder.flush();
            this.outStream.close();
        }
        
        @Override
        public void open(final OutputStream out) throws IOException {
            this.outStream = out;
            this.encoder = EncoderFactory.get().binaryEncoder(out, this.encoder);
        }
        
        @Override
        public void serialize(final T t) throws IOException {
            this.writer.setSchema(AvroSerialization.this.getSchema(t));
            this.writer.write(t, this.encoder);
        }
    }
    
    class AvroDeserializer implements Deserializer<T>
    {
        private DatumReader<T> reader;
        private BinaryDecoder decoder;
        private InputStream inStream;
        
        AvroDeserializer(final Class<T> clazz) {
            this.reader = AvroSerialization.this.getReader(clazz);
        }
        
        @Override
        public void close() throws IOException {
            this.inStream.close();
        }
        
        @Override
        public T deserialize(final T t) throws IOException {
            return this.reader.read(t, this.decoder);
        }
        
        @Override
        public void open(final InputStream in) throws IOException {
            this.inStream = in;
            this.decoder = DecoderFactory.get().binaryDecoder(in, this.decoder);
        }
    }
}
