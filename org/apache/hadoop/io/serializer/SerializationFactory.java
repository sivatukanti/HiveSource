// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.io.serializer.avro.AvroReflectSerialization;
import org.apache.hadoop.io.serializer.avro.AvroSpecificSerialization;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class SerializationFactory extends Configured
{
    static final Logger LOG;
    private List<Serialization<?>> serializations;
    
    public SerializationFactory(final Configuration conf) {
        super(conf);
        this.serializations = new ArrayList<Serialization<?>>();
        for (final String serializerName : conf.getTrimmedStrings("io.serializations", WritableSerialization.class.getName(), AvroSpecificSerialization.class.getName(), AvroReflectSerialization.class.getName())) {
            this.add(conf, serializerName);
        }
    }
    
    private void add(final Configuration conf, final String serializationName) {
        try {
            final Class<? extends Serialization> serializionClass = (Class<? extends Serialization>)conf.getClassByName(serializationName);
            this.serializations.add(ReflectionUtils.newInstance(serializionClass, this.getConf()));
        }
        catch (ClassNotFoundException e) {
            SerializationFactory.LOG.warn("Serialization class not found: ", e);
        }
    }
    
    public <T> Serializer<T> getSerializer(final Class<T> c) {
        final Serialization<T> serializer = this.getSerialization(c);
        if (serializer != null) {
            return serializer.getSerializer(c);
        }
        return null;
    }
    
    public <T> Deserializer<T> getDeserializer(final Class<T> c) {
        final Serialization<T> serializer = this.getSerialization(c);
        if (serializer != null) {
            return serializer.getDeserializer(c);
        }
        return null;
    }
    
    public <T> Serialization<T> getSerialization(final Class<T> c) {
        for (final Serialization serialization : this.serializations) {
            if (serialization.accept(c)) {
                return (Serialization<T>)serialization;
            }
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SerializationFactory.class.getName());
    }
}
