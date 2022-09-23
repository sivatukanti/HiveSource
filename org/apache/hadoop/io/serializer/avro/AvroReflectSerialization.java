// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer.avro;

import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.io.DatumWriter;
import java.lang.reflect.Type;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.io.DatumReader;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AvroReflectSerialization extends AvroSerialization<Object>
{
    @InterfaceAudience.Private
    public static final String AVRO_REFLECT_PACKAGES = "avro.reflect.pkgs";
    private Set<String> packages;
    
    @InterfaceAudience.Private
    @Override
    public synchronized boolean accept(final Class<?> c) {
        if (this.packages == null) {
            this.getPackages();
        }
        return AvroReflectSerializable.class.isAssignableFrom(c) || (c.getPackage() != null && this.packages.contains(c.getPackage().getName()));
    }
    
    private void getPackages() {
        final String[] pkgList = this.getConf().getStrings("avro.reflect.pkgs");
        this.packages = new HashSet<String>();
        if (pkgList != null) {
            for (final String pkg : pkgList) {
                this.packages.add(pkg.trim());
            }
        }
    }
    
    @InterfaceAudience.Private
    @Override
    public DatumReader getReader(final Class<Object> clazz) {
        try {
            return new ReflectDatumReader(clazz);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @InterfaceAudience.Private
    @Override
    public Schema getSchema(final Object t) {
        return ReflectData.get().getSchema(t.getClass());
    }
    
    @InterfaceAudience.Private
    @Override
    public DatumWriter getWriter(final Class<Object> clazz) {
        return new ReflectDatumWriter();
    }
}
